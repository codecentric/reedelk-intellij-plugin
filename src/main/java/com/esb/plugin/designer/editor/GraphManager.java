package com.esb.plugin.designer.editor;

import com.esb.internal.commons.FileUtils;
import com.esb.plugin.designer.editor.component.Component;
import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.builder.FlowGraphBuilder;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.fileEditor.TextEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import java.util.function.Consumer;

import static java.util.Arrays.stream;

public class GraphManager extends DropTarget implements FileEditorManagerListener, DocumentListener {

    private final VirtualFile managedGraphFile;
    private GraphChangeListener listener;
    private final Consumer<FlowGraph> computePositionsAndUpdate = new Consumer<FlowGraph>() {
        @Override
        public void accept(FlowGraph graph) {
            graph.computePositions();
            if (listener != null)
                listener.updated(graph);
        }
    };
    private FlowGraph graph;

    public GraphManager(Project project, VirtualFile managedGraphFile) {
        this.managedGraphFile = managedGraphFile;
        project.getMessageBus()
                .connect()
                .subscribe(FILE_EDITOR_MANAGER, this);
        buildGraph(managedGraphFile).ifPresent(computePositionsAndUpdate.andThen(new Consumer<FlowGraph>() {
            @Override
            public void accept(FlowGraph graph1) {
                graph = graph1;
            }
        }));
    }

    public void addGraphChangeListener(GraphChangeListener listener) {
        this.listener = listener;
        if (graph != null) {
            this.listener.updated(graph);
        }
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

        clearAutoscroll();
        dropEvent.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);

        Component component = new Component(componentName);
        component.setDescription("A description");
        Point location = dropEvent.getLocation();


        // TODO: Here need to decide given the position where this component should go in the tree
    }

    @Override
    public void documentChanged(@NotNull DocumentEvent event) {
        Document document = event.getDocument();
        String json = document.getText();
        buildGraph(json).ifPresent(computePositionsAndUpdate);
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

    // TODO: I would extract this. Because this editor should not be aware of the other
    // TODO: editor!!!
    // TODO: Now it should have been solved
    @Override
    public void fileOpened(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
        // Find Associated Text Editor and attach document listener
        FileEditor[] editors = source.getEditors(file);
        stream(editors).filter(fileEditor -> fileEditor instanceof TextEditor)
                .findFirst()
                .map(fileEditor -> (TextEditor) fileEditor)
                .ifPresent(textEditor -> {
                    Document document = textEditor.getEditor().getDocument();
                    document.addDocumentListener(GraphManager.this);
                });
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

}
