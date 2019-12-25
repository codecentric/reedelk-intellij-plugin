package com.reedelk.plugin.graph.manager;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.concurrency.AppExecutorUtil;
import com.intellij.util.messages.MessageBusConnection;
import com.reedelk.plugin.editor.DesignerEditor;
import com.reedelk.plugin.graph.*;
import com.reedelk.plugin.graph.deserializer.DeserializationError;
import com.reedelk.plugin.service.module.ComponentService;
import com.reedelk.plugin.service.module.impl.component.ModuleComponents;
import com.reedelk.plugin.service.module.impl.component.scanner.ComponentListUpdateNotifier;
import com.reedelk.plugin.topic.ReedelkTopics;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.concurrent.ExecutorService;

import static com.reedelk.plugin.message.ReedelkBundle.message;
import static javax.swing.SwingUtilities.invokeLater;

/**
 * Centralizes updates of the graph coming from:
 * - Text Editor associated with the flow designer (the user manually updates the JSON)
 * - Flow Designer: updates caused by drag and drop and moving around components
 * - Properties Panel: updates cause by component's property changes from an input field
 */
public abstract class GraphManager implements FileEditorManagerListener, FileEditorManagerListener.Before, SnapshotListener, Disposable, ComponentListUpdateNotifier {

    private static final Logger LOG = Logger.getInstance(GraphManager.class);

    // Only one executor across the application to deserialize
    private static ExecutorService executor =
            AppExecutorUtil.createBoundedApplicationPoolExecutor("Reedelk Deserializer", 1);

    private final Module module;
    private final FlowSnapshot snapshot;
    private final VirtualFile graphFile;
    private final FlowGraphProvider graphProvider;
    private final ComponentService componentService;
    private final MessageBusConnection projectBusConnection;
    private final MessageBusConnection moduleBusConnection;

    private Document document;

    GraphManager(@NotNull Module module,
                 @NotNull VirtualFile managedFile,
                 @NotNull FlowSnapshot snapshot,
                 @NotNull FlowGraphProvider graphProvider,
                 @NotNull ComponentService componentService) {
        this.module = module;
        this.snapshot = snapshot;
        this.graphFile = managedFile;
        this.snapshot.addListener(this);
        this.graphProvider = graphProvider;
        this.componentService = componentService;

        projectBusConnection = module.getProject().getMessageBus().connect();
        projectBusConnection.subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, this);

        moduleBusConnection = module.getMessageBus().connect();
        moduleBusConnection.subscribe(ReedelkTopics.COMPONENTS_UPDATE_EVENTS, this);
    }

    @Override
    public void fileOpened(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
        if (file.equals(graphFile)) {
            this.document = FileDocumentManager.getInstance().getDocument(file);
            deserializeDocument();
        }
    }

    @Override
    public void selectionChanged(@NotNull FileEditorManagerEvent event) {
        VirtualFile[] selectedFiles = FileEditorManager.getInstance(module.getProject()).getSelectedFiles();
        for (VirtualFile file : selectedFiles) {
            if (file.equals(graphFile)) {
                if (event.getNewEditor() instanceof DesignerEditor) {
                    deserializeDocument();
                    break;
                }
            }
        }
    }

    @Override
    public void onComponentListUpdate(Collection<ModuleComponents> components) {
        // When the component list is updated, we MUST deserialize so that
        // unknown components are correctly resolved and visualized in the Designer.
        if (componentService.isInitialized()) {
            deserializeDocument();
        }
    }

    @Override
    public void onDataChange() {
        FlowGraph updatedGraph = snapshot.getGraphOrThrowIfAbsent();
        String serializedGraphJson = serialize(updatedGraph);
        write(serializedGraphJson);
    }

    @Override
    public void dispose() {
        this.document = null;
        this.projectBusConnection.disconnect();
        this.moduleBusConnection.disconnect();
    }

    /**
     * Writes the json into the document.
     *
     * @param json the json string to be written in the document.
     */
    private void write(final @NotNull String json) {
        ApplicationManager.getApplication().invokeLater(() ->
                WriteCommandAction.runWriteCommandAction(module.getProject(),
                        () -> document.setText(json)), ModalityState.NON_MODAL);
    }

    private void deserializeDocument() {
        if (document == null) return;

        final String json = document.getText();
        // We only deserialize (in the background) if and only if the
        // document related to the current file has been opened already.
        // Note that if the document has an empty text, the deserialization
        // will just thrown a 'JSONException' since it is not a valid JSON and
        // the designer panel will show an Error screen with the exception message.
        executor.submit(() -> {
            try {
                FlowGraph deSerializedGraph = deserialize(module, json, graphProvider);
                invokeLater(() ->
                        snapshot.updateSnapshot(GraphManager.this, deSerializedGraph));

            } catch (Exception exception) {
                LOG.warn(message("graph.manager.error.deserialization", json, exception.getMessage()), exception);
                FlowGraph deSerializedGraph = new ErrorFlowGraph(exception);
                invokeLater(() ->
                        snapshot.updateSnapshot(GraphManager.this, deSerializedGraph));
            }
        });
    }


    protected abstract String serialize(FlowGraph graph);

    protected abstract FlowGraph deserialize(Module module, String documentText, FlowGraphProvider graphProvider) throws DeserializationError;

}
