package com.esb.plugin.designer.editor;

import com.esb.internal.commons.FileUtils;
import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.builder.FlowGraphBuilder;
import com.esb.plugin.designer.graph.dragdrop.AddComponent;
import com.esb.plugin.designer.graph.drawable.Drawable;
import com.esb.plugin.designer.graph.drawable.DrawableFactory;
import com.esb.plugin.designer.graph.drawable.ScopedDrawable;
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
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static java.awt.dnd.DnDConstants.ACTION_COPY_OR_MOVE;
import static java.util.Arrays.stream;

/**
 * Centralizes updates of the graph coming from the following sources:
 * - The text editor associated with the flow designer (the user manually updates the JSON)
 * - The Canvas updates (drag and drop and moving around components)
 */
public class GraphManager extends DropTarget implements DesignerPanelDropListener, FileEditorManagerListener, DocumentListener, Disposable {

    private FlowGraph graph;
    private GraphChangeListener listener;
    private MessageBusConnection busConnection;

    GraphManager(Project project, VirtualFile managedGraphFile) {
        this.busConnection = project.getMessageBus().connect();
        this.busConnection.subscribe(FILE_EDITOR_MANAGER, this);

        buildGraph(managedGraphFile)
                .ifPresent(((Consumer<FlowGraph>) fromFileGraph -> graph = fromFileGraph)
                        .andThen(graph -> notifyGraphUpdated()));
    }

    @Override
    public void documentChanged(@NotNull DocumentEvent event) {
        Document document = event.getDocument();
        String json = document.getText();
        buildGraph(json)
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
    public synchronized void drop(DropTargetDropEvent dropEvent) {

        try {
            String componentName = (String) dropEvent.getTransferable().getTransferData(DataFlavor.stringFlavor);

            Point location = dropEvent.getLocation();
            Drawable componentToAdd = DrawableFactory.get(componentName);

            AddComponent nodeAdder = new AddComponent(graph, location, componentToAdd);
            Optional<FlowGraph> modifiedGraph = nodeAdder.add();

            if (modifiedGraph.isPresent()) {
                modifiedGraph.ifPresent(((Consumer<FlowGraph>) updatedGraph -> graph = updatedGraph)
                        .andThen(updatedGraph -> dropEvent.acceptDrop(ACTION_COPY_OR_MOVE))
                        .andThen(updatedGraph -> notifyGraphUpdated()));
            } else {
                // Drop rejected if the graph was not modified.
                dropEvent.rejectDrop();
            }
        } catch (UnsupportedFlavorException | IOException e) {
            dropEvent.rejectDrop();
        }
    }

    @Override
    public void dispose() {
        this.busConnection.disconnect();
    }

    @Override
    public void drop(int x, int y, Drawable dropped) {
        // Get the predecessors of the node and connect it to the successors
        List<Drawable> predecessors = graph.predecessors(dropped);
        List<Drawable> successors = graph.successors(dropped);
        if (predecessors.isEmpty()) {
            graph.root(successors.get(0));
        } else {
            for (Drawable predecessor : predecessors) {
                for (Drawable successor : successors) {
                    graph.add(predecessor, successor);
                }
            }
        }
        graph.remove(dropped);

        // We remove the node from any scope it might belong to
        removeFromAnyScope(dropped);

        // Need to copy over the node, and remove it from its previous place.
        AddComponent componentAdder = new AddComponent(graph, new Point(x, y), dropped);
        Optional<FlowGraph> addedNode = componentAdder.add();

        addedNode
                .ifPresent(((Consumer<FlowGraph>) updatedGraph -> graph = updatedGraph)
                        .andThen(updatedGraph -> notifyGraphUpdated()));
    }

    private void removeFromAnyScope(Drawable dropped) {
        graph.breadthFirstTraversal(drawable -> {
            if (drawable instanceof ScopedDrawable) {
                ((ScopedDrawable) drawable).removeFromScope(dropped);
            }
        });
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
