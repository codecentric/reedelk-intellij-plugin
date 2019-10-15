package com.reedelk.plugin.graph.manager;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ThrowableRunnable;
import com.intellij.util.concurrency.SwingWorker;
import com.intellij.util.messages.MessageBusConnection;
import com.reedelk.plugin.component.scanner.ComponentListUpdateNotifier;
import com.reedelk.plugin.editor.DesignerEditor;
import com.reedelk.plugin.graph.*;
import com.reedelk.plugin.graph.deserializer.DeserializationError;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

import static com.reedelk.plugin.service.project.DesignerSelectionManager.CurrentSelectionListener;
import static org.apache.commons.lang3.StringUtils.isBlank;

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
        moduleBusConnection.subscribe(COMPONENT_LIST_UPDATE_TOPIC, this);

        currentSelectionPublisher = module.getProject().getMessageBus()
                .syncPublisher(CurrentSelectionListener.CURRENT_SELECTION_TOPIC);
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
    public void onComponentListUpdate(Module module) {
        SwingUtilities.invokeLater(this::deserializeDocument);
    }

    @Override
    public void onDataChange() {
        FlowGraph updatedGraph = snapshot.getGraphOrThrowIfAbsent();
        String json = serialize(updatedGraph);
        write(json);
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
    private void write(String json) {
        try {
            WriteCommandAction.writeCommandAction(module.getProject())
                    .run((ThrowableRunnable<Throwable>) () -> document.setText(json));
        } catch (Throwable throwable) {
            LOG.error("Could not write Graph's JSON data", throwable);
        }
    }

    private void deserializeDocument() {
        // We only deserialize if the document is present and the text
        // is not empty.
        if (document != null && !isBlank(document.getText())) {
            deserializeGraphAndNotify();
        }
    }

    protected abstract String serialize(FlowGraph graph);

    protected abstract FlowGraph deserialize(Module module, Document document, FlowGraphProvider graphProvider) throws DeserializationError;

    private void deserializeGraphAndNotify() {
        new DeserializeGraphAndNotify().start();
    }

    private class DeserializeGraphAndNotify extends SwingWorker {
        @Override
        public FlowGraph construct() {
            // Background Thread. We are assuming that deserialization
            // of the graph from JSON is a lengthy operation.
            try {
                return deserialize(module, document, graphProvider);
            } catch (DeserializationError e) {
                LOG.warn("Deserialization error", e);
                return new ErrorFlowGraph(e);
            } catch (Exception e) {
                LOG.warn("Error", e);
                return new ErrorFlowGraph(e);
            }
        }

        @Override
        public void finished() {
            // AWT Thread dispatch
            FlowGraph graph = (FlowGraph) get();
            snapshot.updateSnapshot(GraphManager.this, graph);
            // We refresh the current selection only if the graph is not in error.
            if (!graph.isError()) {
                // When we deserialize the document we must refresh the current selection,
                // so that the Properties panel always references the correct and latest
                // graph data.
                currentSelectionPublisher.refresh();
            }
        }
    }
}