package com.esb.plugin.designer.editor;

import com.esb.internal.commons.FileUtils;
import com.esb.plugin.designer.editor.designer.DesignerPanelDropTarget;
import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.builder.FlowGraphBuilder;
import com.intellij.codeHighlighting.BackgroundEditorHighlighter;
import com.intellij.codeHighlighting.HighlightingPass;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.project.PossiblyDumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.UserDataHolderBase;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.beans.PropertyChangeListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import java.util.function.Consumer;

import static java.util.Arrays.stream;

public class FlowEditor extends UserDataHolderBase implements FileEditor, PossiblyDumbAware, FileEditorManagerListener, DocumentListener {

    private FlowEditorPanel editor;
    private VirtualFile editorFile;

    private final Consumer<FlowGraph> computePositionsAndUpdate = new Consumer<FlowGraph>() {
        @Override
        public void accept(FlowGraph graph) {
            graph.computePositions();
            editor.updated(graph);
        }
    };

    FlowEditor(Project project, VirtualFile file) {

        DesignerPanelDropTarget designerPanelDropTarget = new DesignerPanelDropTarget();

        this.editor = new FlowEditorPanel(designerPanelDropTarget); // remove passing file to the editor panel
        this.editorFile = file;

        buildGraph(file).ifPresent(computePositionsAndUpdate);

        project.getMessageBus()
                .connect(this)
                .subscribe(FILE_EDITOR_MANAGER, this);
    }

    @NotNull
    @Override
    public JComponent getComponent() {
        return editor;
    }

    @Nullable
    @Override
    public JComponent getPreferredFocusedComponent() {
        return editor;
    }

    @NotNull
    @Override
    public String getName() {
        return "Flow Designer";
    }

    @Override
    public void setState(@NotNull FileEditorState state) {

    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public void selectNotify() {

    }

    @Override
    public void deselectNotify() {

    }

    @Override
    public void addPropertyChangeListener(@NotNull PropertyChangeListener listener) {

    }

    @Override
    public void removePropertyChangeListener(@NotNull PropertyChangeListener listener) {

    }

    @Nullable
    @Override
    public BackgroundEditorHighlighter getBackgroundHighlighter() {
        return new BackgroundEditorHighlighter() {
            @NotNull
            @Override
            public HighlightingPass[] createPassesForEditor() {
                return new HighlightingPass[0];
            }

            @NotNull
            @Override
            public HighlightingPass[] createPassesForVisibleArea() {
                return new HighlightingPass[0];
            }
        };
    }

    @Nullable
    @Override
    public FileEditorLocation getCurrentLocation() {
        return new FileEditorLocation() {
            @NotNull
            @Override
            public FileEditor getEditor() {
                return FlowEditor.this;
            }

            @Override
            public int compareTo(@NotNull FileEditorLocation o) {
                return 0;
            }
        };
    }

    @Override
    public void dispose() {

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
    @Override
    public void fileOpened(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
        if (!editorFile.getUrl().equals(file.getUrl())) return;


        // Find Associated Text Editor and attach document listener
        FileEditor[] editors = source.getEditors(file);
        stream(editors).filter(fileEditor -> fileEditor instanceof TextEditor)
                .findFirst()
                .map(fileEditor -> (TextEditor) fileEditor)
                .ifPresent(textEditor -> {
                    Document document = textEditor.getEditor().getDocument();
                    document.addDocumentListener(FlowEditor.this);
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
