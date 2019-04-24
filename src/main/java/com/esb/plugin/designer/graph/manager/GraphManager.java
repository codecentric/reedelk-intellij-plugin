package com.esb.plugin.designer.graph.manager;

import com.esb.internal.commons.FileUtils;
import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.FlowGraphChangeListener;
import com.esb.plugin.designer.graph.builder.FlowGraphBuilder;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.fileEditor.TextEditor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.messages.MessageBusConnection;
import org.jetbrains.annotations.NotNull;

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
public class GraphManager implements FileEditorManagerListener, DocumentListener, Disposable {

    private FlowGraph graph;
    private FlowGraphChangeListener listener;
    private MessageBusConnection busConnection;
    private Module module;

    public GraphManager(Project project, VirtualFile managedGraphFile) {
        this.busConnection = project.getMessageBus().connect();
        this.busConnection.subscribe(FILE_EDITOR_MANAGER, this);

        module = ModuleUtil.findModuleForFile(managedGraphFile, project);
        buildFlowGraph(module, managedGraphFile)
                .ifPresent(((Consumer<FlowGraph>) fromFileGraph -> graph = fromFileGraph)
                        .andThen(graph -> notifyGraphUpdated()));
    }

    @Override
    public void documentChanged(@NotNull DocumentEvent event) {
        Document document = event.getDocument();
        String json = document.getText();
        buildFlowGraph(module, json)
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

    public void addGraphChangeListener(FlowGraphChangeListener listener) {
        this.listener = listener;
        if (graph != null) {
            this.listener.updated(graph);
        }
    }

    private Optional<FlowGraph> buildFlowGraph(Module module, VirtualFile file) {
        try {
            String json = FileUtils.readFrom(new URL(file.getUrl()));
            return buildFlowGraph(module, json);
        } catch (MalformedURLException e) {
            return Optional.empty();
        }
    }

    private Optional<FlowGraph> buildFlowGraph(Module module, String json) {
        try {
            FlowGraphBuilder builder = new FlowGraphBuilder(json);
            return Optional.of(builder.graph(module));
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
