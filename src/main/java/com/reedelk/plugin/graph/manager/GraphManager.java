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
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.messages.MessageBusConnection;
import com.reedelk.plugin.editor.DesignerEditor;
import com.reedelk.plugin.executor.AsyncProgressTask;
import com.reedelk.plugin.executor.PluginExecutors;
import com.reedelk.plugin.graph.*;
import com.reedelk.plugin.graph.deserializer.DeserializationError;
import com.reedelk.plugin.service.module.ComponentService;
import com.reedelk.plugin.service.module.impl.component.scanner.ComponentListUpdateNotifier;
import com.reedelk.plugin.topic.ReedelkTopics;
import org.jetbrains.annotations.NotNull;

import static com.reedelk.plugin.message.ReedelkBundle.message;
import static com.reedelk.plugin.service.project.DesignerSelectionService.CurrentSelectionListener;

/**
 * Centralizes updates of the graph coming from:
 * - Text Editor associated with the flow designer (the user manually updates the JSON)
 * - Flow Designer: updates caused by drag and drop and moving around components
 * - Properties Panel: updates cause by component's property changes from an input field
 */
public abstract class GraphManager implements FileEditorManagerListener, FileEditorManagerListener.Before, SnapshotListener, Disposable, ComponentListUpdateNotifier {

    private static final Logger LOG = Logger.getInstance(GraphManager.class);

    private final Module module;
    private final FlowSnapshot snapshot;
    private final VirtualFile graphFile;
    private final FlowGraphProvider graphProvider;
    private final MessageBusConnection projectBusConnection;
    private final MessageBusConnection moduleBusConnection;

    private final CurrentSelectionListener currentSelectionPublisher;
    private Document document;

    GraphManager(@NotNull Module module,
                 @NotNull VirtualFile managedFile,
                 @NotNull FlowSnapshot snapshot,
                 @NotNull FlowGraphProvider graphProvider) {
        this.module = module;
        this.snapshot = snapshot;
        this.graphFile = managedFile;
        this.snapshot.addListener(this);
        this.graphProvider = graphProvider;

        projectBusConnection = module.getProject().getMessageBus().connect();
        projectBusConnection.subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, this);

        moduleBusConnection = module.getMessageBus().connect();
        moduleBusConnection.subscribe(ReedelkTopics.COMPONENTS_UPDATE_EVENTS, this);

        currentSelectionPublisher = module.getProject().getMessageBus()
                .syncPublisher(ReedelkTopics.CURRENT_COMPONENT_SELECTION_EVENTS);
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
    public void onComponentListUpdate() {
        // When the component list is updated, we MUST deserialize so that
        // unknown components are correctly resolved and visualized in the Designer.
        deserializeDocument();
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
        // We only deserialize (in the background) if and only if the
        // document related to the current file has been opened already.
        // Note that if the document has an empty text, the deserialization
        // will just thrown a 'JSONException' since it is not a valid JSON and
        // the designer panel will show an Error screen with the exception message.
        if (document != null) {
            PluginExecutors.run(module, new DeserializeGraphAndNotify());
        }
    }

    protected abstract String serialize(FlowGraph graph);

    protected abstract FlowGraph deserialize(Module module, Document document, FlowGraphProvider graphProvider) throws DeserializationError;

    private class DeserializeGraphAndNotify implements AsyncProgressTask {

        private FlowGraph deSerializedGraph;
        private boolean cancelled = false;

        @Override
        public void run(@NotNull ProgressIndicator indicator) {
            indicator.setText("De-serializing flow");
            if (!ComponentService.getInstance(module).isInitialized()) {
                indicator.cancel();
                return;
            }
            try {
                // We are assuming that deserialization of the graph from JSON is a lengthy operation.
                // Therefore we execute it in a background thread.
                this.deSerializedGraph = deserialize(module, document, graphProvider);
            } catch (Exception exception) {
                LOG.warn(message("graph.manager.error.deserialization", document.getText(), exception.getMessage()), exception);
                this.deSerializedGraph = new ErrorFlowGraph(exception);
            }
        }

        @Override
        public void onThrowable(@NotNull Throwable error) {
            LOG.warn(message("graph.manager.error.deserialization", document.getText(), error.getMessage()), error);
            this.deSerializedGraph = new ErrorFlowGraph(error);
        }

        @Override
        public void onCancel() {
            this.cancelled = true;
        }

        @Override
        public void onFinished() {
            if (cancelled) return;
            snapshot.updateSnapshot(GraphManager.this, this.deSerializedGraph);
            // We refresh the current selection only if the graph is not in error.
            if (!this.deSerializedGraph.isError()) {
                // When we deserialize the document we must refresh the current selection,
                // so that the Properties panel always references the correct and latest
                // graph data.
                currentSelectionPublisher.refresh();
            }
        }
    }
}
