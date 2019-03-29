package com.esb.plugin.designer.editor;

import com.esb.internal.commons.FileUtils;
import com.esb.plugin.designer.editor.component.Component;
import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.builder.FlowGraphBuilder;
import com.esb.plugin.designer.graph.drawable.Drawable;
import com.esb.plugin.designer.graph.drawable.GenericComponentDrawable;
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
        String componentName;
        try {
            componentName = (String) dropEvent.getTransferable().getTransferData(DataFlavor.stringFlavor);
        } catch (UnsupportedFlavorException | IOException e) {
            dropEvent.rejectDrop();
            return;
        }

        //clearAutoscroll();
        dropEvent.acceptDrop(ACTION_COPY_OR_MOVE);

        Component component = new Component(componentName);
        component.setDescription("A description");


        Point location = dropEvent.getLocation();
        int dropX = location.x;
        int dropY = location.y;

        // TODO: Here need to decide given the position where this component should go in the tree

        FlowGraph copy = graph.copy();

        // If graph is empty
        // - add root

        // DETECT THE POSITION

        for (Drawable node : graph.nodes()) {
            if (dropX > node.x()) {

                GenericComponentDrawable genericDrawable = new GenericComponentDrawable(component);

                List<Drawable> successors = graph.successors(node);
                if (successors.isEmpty()) {
                    // Last node of the subtree, OK
                    copy.add(node, genericDrawable);
                }
                for (Drawable successor : successors) {
                    if (dropX < successor.x()) {
                        copy.add(node, genericDrawable);
                        copy.remove(node, successor);
                        copy.add(genericDrawable, successor);
                    }
                }
            }
        }

        Optional.of(copy)
                .ifPresent(((Consumer<FlowGraph>) fromFileGraph -> graph = fromFileGraph)
                        .andThen(graph -> computeGraphPositionsAndNotifyChange()));
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
