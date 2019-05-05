package com.esb.plugin.graph.manager;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.deserializer.GraphDeserializer;
import com.esb.plugin.graph.serializer.GraphSerializer;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.fileEditor.TextEditor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ThrowableRunnable;
import com.intellij.util.messages.MessageBusConnection;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Consumer;

import static java.util.Arrays.stream;

/**
 * Centralizes updates of the graph coming from the following sources:
 * - The text editor associated with the flow designer (the user manually updates the JSON)
 * - Designer updates:
 * - drag and drop and moving around components
 * - Properties updates:
 * - component's property changed
 */
public class GraphManager implements FileEditorManagerListener, DocumentListener, GraphChangeNotifier, Disposable {

    private static final Logger LOG = Logger.getInstance(GraphManager.class);

    private Module module;
    private FlowGraph graph;
    private final Project project;
    private VirtualFile jsonGraphFile;
    private MessageBusConnection busConnection;

    public GraphManager(Project project, Module module, VirtualFile jsonGraphFile) {
        this.module = module;
        this.project = project;
        this.jsonGraphFile = jsonGraphFile;

        busConnection = module.getMessageBus().connect();
        busConnection.subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, this);
        busConnection.subscribe(GraphChangeNotifier.TOPIC, this);

        GraphDeserializer.deserialize(module, jsonGraphFile)
                .ifPresent(((Consumer<FlowGraph>) fromFileGraph -> graph = fromFileGraph)
                        .andThen(graph -> notifyGraphUpdated()));
    }

    /**
     * The JSON representing the graph was manually changed by the user.
     * The graph must be re-built.
     *
     * @param event the change object triggering this callback.
     */
    @Override
    public void documentChanged(@NotNull DocumentEvent event) {
        Document document = event.getDocument();
        String graphAsJson = document.getText();
        GraphDeserializer.deserialize(module, graphAsJson)
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
    public void onChange(FlowGraph graph, VirtualFile virtualFile) {
        if (!virtualFile.equals(jsonGraphFile)) return;

        // Serialize the graph to json and write it into the file
        String json = GraphSerializer.serialize(graph);
        try {
            WriteCommandAction
                    .writeCommandAction(project)
                    .run((ThrowableRunnable<Throwable>) () ->
                            jsonGraphFile.setBinaryContent(json.getBytes()));
        } catch (Throwable throwable) {
            LOG.error("Could not write Graph's JSON data", throwable);
        }
    }

    @Override
    public void dispose() {
        busConnection.disconnect();
    }

    private void notifyGraphUpdated() {
        JsonChangeNotifier notifier = module.getMessageBus().syncPublisher(JsonChangeNotifier.TOPIC);
        notifier.onChange(graph, jsonGraphFile);
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
