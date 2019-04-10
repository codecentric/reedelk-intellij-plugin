package com.esb.plugin.designer.graph.manager;

import com.esb.internal.commons.FileUtils;
import com.esb.plugin.designer.graph.DropListener;
import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.FlowGraphChangeListener;
import com.esb.plugin.designer.graph.builder.FlowGraphBuilder;
import com.esb.plugin.designer.graph.drawable.Drawable;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.fileEditor.TextEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.messages.MessageBusConnection;
import org.jetbrains.annotations.NotNull;

import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import java.util.function.Consumer;

import static java.util.Arrays.stream;

/**
 * Centralizes updates of the graph coming from the following sources:
 * - The text editor associated with the flow designer (the user manually updates the JSON)
 * - The Canvas updates (drag and drop and moving around components)
 */
public class GraphManager extends DropTarget implements DropListener, FileEditorManagerListener, DocumentListener, Disposable {

    private FlowGraph graph;
    private FlowGraphChangeListener listener;
    private MessageBusConnection busConnection;
    private MoveDropTarget moveDropTargetDelegate;
    private PaletteDropTarget paletteDropTargetDelegate;

    public GraphManager(Project project, VirtualFile managedGraphFile) {
        this.moveDropTargetDelegate = new MoveDropTarget();
        this.paletteDropTargetDelegate = new PaletteDropTarget();
        this.busConnection = project.getMessageBus().connect();
        this.busConnection.subscribe(FILE_EDITOR_MANAGER, this);

        buildFlowGraph(managedGraphFile)
                .ifPresent(((Consumer<FlowGraph>) fromFileGraph -> graph = fromFileGraph)
                        .andThen(graph -> notifyGraphUpdated()));
    }

    @Override
    public void documentChanged(@NotNull DocumentEvent event) {
        Document document = event.getDocument();
        String json = document.getText();
        buildFlowGraph(json)
                .ifPresent(((Consumer<FlowGraph>) fromFileGraph -> graph = fromFileGraph)
                        .andThen(graph -> notifyGraphUpdated()));
    }

    @Override
    public void fileOpened(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
        findRelatedEditorDocument(source, file)
                .ifPresent(document -> document.addDocumentListener(GraphManager.this));
    }

    @Override
    public void fileClosed(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
        findRelatedEditorDocument(source, file)
                .ifPresent(document -> document.removeDocumentListener(GraphManager.this));
    }

    @Override
    public void dispose() {
        this.busConnection.disconnect();
    }

    /* *
     *
     * Called when we drop a component from the PALETTE
     */
    @Override
    public synchronized void drop(DropTargetDropEvent dropEvent) {
        paletteDropTargetDelegate
                .drop(dropEvent, graph)
                .ifPresent(updatedGraph -> {
                    graph = updatedGraph;
                    notifyGraphUpdated();
                });
    }

    /**
     * Called when we drop a drawable into the canvas from a move operation.
     */
    @Override
    public void drop(int x, int y, Drawable dropped) {
        moveDropTargetDelegate
                .drop(x, y, graph, dropped)
                .ifPresent(updatedGraph -> {
                    graph = updatedGraph;
                    notifyGraphUpdated();
                });
    }

    public void addGraphChangeListener(FlowGraphChangeListener listener) {
        this.listener = listener;
        if (graph != null) {
            this.listener.updated(graph);
        }
    }

    private Optional<FlowGraph> buildFlowGraph(VirtualFile file) {
        try {
            String json = FileUtils.readFrom(new URL(file.getUrl()));
            return buildFlowGraph(json);
        } catch (MalformedURLException e) {
            return Optional.empty();
        }
    }

    private Optional<FlowGraph> buildFlowGraph(String json) {
        try {
            FlowGraphBuilder builder = new FlowGraphBuilder(json);
            return Optional.of(builder.graph());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private void notifyGraphUpdated() {
        if (listener != null) {
            listener.updated(graph);
        }
    }

    /**
     * Finds the Document Object this Editor's GraphManager is referring to.
     */
    private Optional<Document> findRelatedEditorDocument(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
        FileEditor[] editors = source.getEditors(file);
        return stream(editors).filter(fileEditor -> fileEditor instanceof TextEditor)
                .findFirst()
                .map(fileEditor -> (TextEditor) fileEditor)
                .map(textEditor -> textEditor.getEditor().getDocument());
    }

}
