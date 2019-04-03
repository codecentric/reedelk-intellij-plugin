package com.esb.plugin.designer.editor;

import com.esb.internal.commons.FileUtils;
import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.builder.FlowGraphBuilder;
import com.esb.plugin.designer.graph.dnd.GraphNodeAdder;
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

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import java.util.function.Consumer;

import static java.awt.dnd.DnDConstants.ACTION_COPY_OR_MOVE;
import static java.util.Arrays.stream;

/**
 * Centralizes updates of the graph coming from the following sources:
 * - The text editor associated with the flow designer (the user manually updates the JSON)
 * - The Canvas updates (drag and drop and moving around components)
 */
public class GraphManager extends DropTarget implements FileEditorManagerListener, DocumentListener, Disposable {

    private FlowGraph graph;
    private GraphChangeListener listener;
    private MessageBusConnection busConnection;

    GraphManager(Project project, VirtualFile managedGraphFile) {
        this.busConnection = project.getMessageBus().connect();
        this.busConnection.subscribe(FILE_EDITOR_MANAGER, this);

        buildGraph(managedGraphFile)
                .ifPresent(((Consumer<FlowGraph>) fromFileGraph -> graph = fromFileGraph)
                        .andThen(graph -> computeGraphPositionsAndNotifyChange()));
    }

    @Override
    public void documentChanged(@NotNull DocumentEvent event) {
        Document document = event.getDocument();
        String json = document.getText();
        buildGraph(json)
                .ifPresent(((Consumer<FlowGraph>) fromFileGraph -> graph = fromFileGraph)
                        .andThen(graph -> computeGraphPositionsAndNotifyChange()));
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
    public synchronized void drop(DropTargetDropEvent dropEvent) {

        try {
            String componentName = (String) dropEvent.getTransferable().getTransferData(DataFlavor.stringFlavor);

            Point location = dropEvent.getLocation();
            GraphNodeAdder nodeAdder = new GraphNodeAdder(graph, location, componentName);
            Optional<FlowGraph> modifiedGraph = nodeAdder.add();

            if (modifiedGraph.isPresent()) {
                modifiedGraph.ifPresent(((Consumer<FlowGraph>) updatedGraph -> graph = updatedGraph)
                        .andThen(updatedGraph -> dropEvent.acceptDrop(ACTION_COPY_OR_MOVE))
                        .andThen(updatedGraph -> computeGraphPositionsAndNotifyChange()));
            } else {
                // Drop rejected if the graph was not modified.
                dropEvent.rejectDrop();
            }
        } catch (UnsupportedFlavorException | IOException e) {
            dropEvent.rejectDrop();
        }
    }

    void addGraphChangeListener(GraphChangeListener listener) {
        this.listener = listener;
        if (graph != null) {
            this.listener.updated(graph);
        }
    }

    private Optional<FlowGraph> buildGraph(VirtualFile file) {
        try {
            String json = FileUtils.readFrom(new URL(file.getUrl()));
            return buildGraph(json);
        } catch (MalformedURLException e) {
            // TODO: Log this
            e.printStackTrace();
            return Optional.empty();
        }
    }

    private Optional<FlowGraph> buildGraph(String json) {
        try {
            FlowGraphBuilder builder = new FlowGraphBuilder(json);
            return Optional.of(builder.graph());
        } catch (Exception e) {
            // TODO: Log this
            e.printStackTrace();
            return Optional.empty();
        }
    }

    private void computeGraphPositionsAndNotifyChange() {
        graph.computePositions();
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


    @Override
    public void dispose() {
        this.busConnection.disconnect();
    }
}
