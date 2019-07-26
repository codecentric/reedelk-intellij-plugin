package com.esb.plugin.graph.manager;

import com.esb.plugin.component.scanner.ComponentListUpdateNotifier;
import com.esb.plugin.editor.FlowDesignerEditor;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.FlowGraphProvider;
import com.esb.plugin.graph.FlowSnapshot;
import com.esb.plugin.graph.SnapshotListener;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ThrowableRunnable;
import com.intellij.util.messages.MessageBusConnection;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

import static com.esb.plugin.service.project.DesignerSelectionManager.CurrentSelectionListener;
import static java.util.Arrays.stream;

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
            findRelatedEditorDocument(source, file).ifPresent(document -> {
                this.document = document;
                deserializeDocument();
            });
        }
    }

    @Override
    public void selectionChanged(@NotNull FileEditorManagerEvent event) {
        VirtualFile[] selectedFiles = FileEditorManager.getInstance(module.getProject()).getSelectedFiles();
        for (VirtualFile file : selectedFiles) {
            if (file.equals(graphFile)) {
                if (event.getNewEditor() instanceof FlowDesignerEditor) {
                    deserializeDocument();
                    break;
                }
            }
        }
    }

    @Override
    public void onComponentListUpdate(Module module) {
        deserializeDocument();
    }

    @Override
    public void onDataChange() {
        FlowGraph updatedGraph = snapshot.getGraph();
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

    /**
     * Finds the Document Object this Editor's GraphManager is referring to.
     */
    private Optional<Document> findRelatedEditorDocument(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
        FileEditor[] editors = source.getEditors(file);
        return stream(editors)
                .filter(fileEditor -> fileEditor instanceof TextEditor)
                .findFirst()
                .map(fileEditor -> (TextEditor) fileEditor)
                .map(textEditor -> textEditor.getEditor().getDocument());
    }

    private void deserializeDocument() {
        if (document == null) return;
        if (StringUtils.isBlank(document.getText())) return;

        deserialize(module, document, graphProvider).ifPresent(updatedGraph -> {
            snapshot.updateSnapshot(this, updatedGraph);
            // When we deserialize the document we must refresh the current selection,
            // so that the Properties panel always references the correct graph data.
            currentSelectionPublisher.refresh();
        });
    }

    protected abstract String serialize(FlowGraph graph);

    protected abstract Optional<FlowGraph> deserialize(Module module, Document document, FlowGraphProvider graphProvider);

}
