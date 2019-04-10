package com.esb.plugin.designer.editor;

import com.esb.internal.commons.FileUtils;
import com.esb.plugin.designer.graph.AddComponent;
import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.FlowGraphImpl;
import com.esb.plugin.designer.graph.builder.FlowGraphBuilder;
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
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static com.google.common.base.Preconditions.checkState;
import static java.awt.datatransfer.DataFlavor.stringFlavor;
import static java.awt.dnd.DnDConstants.ACTION_COPY_OR_MOVE;
import static java.util.Arrays.asList;
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
        this.graph = new FlowGraphImpl();
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

    @Override
    public synchronized void drop(DropTargetDropEvent dropEvent) {

        Transferable transferable = dropEvent.getTransferable();

        DataFlavor[] transferDataFlavor = transferable.getTransferDataFlavors();
        if (!asList(transferDataFlavor).contains(stringFlavor)) {
            dropEvent.rejectDrop();
            return;
        }

        String componentName = null;
        try {
            componentName = (String) transferable.getTransferData(stringFlavor);
        } catch (UnsupportedFlavorException | IOException e) {
            dropEvent.rejectDrop();
        }

        checkState(componentName != null, "Component name");
        checkState(graph != null, "Graph must not be null");

        Point location = dropEvent.getLocation();
        Drawable componentToAdd = DrawableFactory.get(componentName);

        FlowGraph modifiableGraph = graph.copy();
        AddComponent nodeAdder = new AddComponent(modifiableGraph, location, componentToAdd);
        boolean modified = nodeAdder.add();

        if (modified) {
            graph = modifiableGraph;
            dropEvent.acceptDrop(ACTION_COPY_OR_MOVE);
            notifyGraphUpdated();
        } else {
            dropEvent.rejectDrop();
        }
    }

    @Override
    public void drop(int x, int y, Drawable dropped) {
        // Steps when we drop:

        // 1. Copy the original graph
        FlowGraph modifiableGraph = graph.copy();

        // 2. Remove the dropped node from the copy graph
        // Get the predecessors of the node and connect it to the successors
        List<Drawable> predecessors = graph.predecessors(dropped);
        List<Drawable> successors = graph.successors(dropped);
        if (predecessors.isEmpty()) {
            modifiableGraph.root(successors.get(0));
        } else {
            for (Drawable predecessor : predecessors) {
                for (Drawable successor : successors) {
                    modifiableGraph.add(predecessor, successor);
                }
            }
        }

        modifiableGraph.remove(dropped);

        // 3. Remove the dropped node from any scope it might belong to
        modifiableGraph.breadthFirstTraversal(drawable -> {
            if (drawable instanceof ScopedDrawable) {
                ((ScopedDrawable) drawable).removeFromScope(dropped);
            }
        });

        // 4. Add the dropped component back to the graph to the dropped position.
        AddComponent componentAdder = new AddComponent(graph, new Point(x, y), dropped);
        boolean modified = componentAdder.add();

        // 5. If the copy of the graph was changed, then update the graph
        if (modified) {
            graph = modifiableGraph;
            notifyGraphUpdated();
        }

    }


    void addGraphChangeListener(GraphChangeListener listener) {
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
