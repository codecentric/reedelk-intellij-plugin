package com.esb.plugin.graph.manager;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.FlowGraphProvider;
import com.esb.plugin.graph.GraphSnapshot;
import com.esb.plugin.graph.SnapshotListener;
import com.esb.plugin.graph.deserializer.GraphDeserializer;
import com.esb.plugin.graph.serializer.GraphSerializer;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.fileEditor.TextEditor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.AncestorListenerAdapter;
import com.intellij.util.ThrowableRunnable;
import com.intellij.util.messages.MessageBusConnection;
import org.jetbrains.annotations.NotNull;

import javax.swing.event.AncestorEvent;
import java.util.Optional;

import static java.util.Arrays.stream;

/**
 * Centralizes updates of the graph coming from the following sources:
 * - The text editor associated with the flow designer (the user manually updates the JSON)
 * - Designer updates:
 * - drag and drop and moving around components
 * - Properties updates:
 * - component's property changed
 */
public class GraphManager extends AncestorListenerAdapter implements FileEditorManagerListener, SnapshotListener {

    private static final Logger LOG = Logger.getInstance(GraphManager.class);

    private final Module module;
    private final Project project;
    private final VirtualFile graphFile;
    private final GraphSnapshot snapshot;
    private final FlowGraphProvider graphProvider;

    private Document document;

    public GraphManager(@NotNull Project project, @NotNull Module module, @NotNull VirtualFile graphFile, @NotNull GraphSnapshot snapshot, @NotNull FlowGraphProvider graphProvider) {
        this.module = module;
        this.project = project;
        this.snapshot = snapshot;
        this.graphFile = graphFile;
        this.snapshot.addListener(this);
        this.graphProvider = graphProvider;

        MessageBusConnection busConnection = project.getMessageBus().connect();
        busConnection.subscribe(FILE_EDITOR_MANAGER, this);
    }

    @Override
    public void fileOpened(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
        if (file.equals(graphFile)) {
            findRelatedEditorDocument(source, file).ifPresent(document -> {
                        this.document = document;
                GraphDeserializer.deserialize(module, document.getText(), graphProvider)
                                .ifPresent(updatedGraph -> snapshot.updateSnapshot(this, updatedGraph));
                    });
        }
    }

    @Override
    public void fileClosed(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
        if (file.equals(graphFile)) {
            this.document = null;
        }
    }

    @Override
    public void onDataChange(@NotNull FlowGraph graph) {
        String json = GraphSerializer.serialize(graph);
        write(json);
    }

    @Override
    public void onStructureChange(@NotNull FlowGraph graph) {
        String json = GraphSerializer.serialize(graph);
        write(json);
    }

    @Override
    public void ancestorAdded(AncestorEvent event) {
        if (document != null) {
            GraphDeserializer.deserialize(module, document.getText(), graphProvider)
                    .ifPresent(updatedGraph -> snapshot.updateSnapshot(this, updatedGraph));
        }
    }

    /**
     * Writes the json into the document.
     *
     * @param json the json string to be written in the document.
     */
    private void write(String json) {
        try {
            WriteCommandAction.writeCommandAction(project)
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
        return stream(editors).filter(fileEditor -> fileEditor instanceof TextEditor)
                .findFirst()
                .map(fileEditor -> (TextEditor) fileEditor)
                .map(textEditor -> textEditor.getEditor().getDocument());
    }
}
