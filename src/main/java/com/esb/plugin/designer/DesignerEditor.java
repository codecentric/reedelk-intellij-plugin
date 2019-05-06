package com.esb.plugin.designer;

import com.esb.plugin.graph.manager.GraphManager;
import com.intellij.codeHighlighting.BackgroundEditorHighlighter;
import com.intellij.codeHighlighting.HighlightingPass;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorLocation;
import com.intellij.openapi.fileEditor.FileEditorState;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.PossiblyDumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.UserDataHolderBase;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.beans.PropertyChangeListener;

import static com.google.common.base.Preconditions.checkState;

public class DesignerEditor extends UserDataHolderBase implements FileEditor, PossiblyDumbAware, DocumentListener {

    private DesignerEditorPanel editor;
    private GraphManager manager;

    DesignerEditor(Project project, VirtualFile file) {
        Module module = ModuleUtil.findModuleForFile(file, project);
        checkState(module != null, "Module must not be null");
        editor = new DesignerEditorPanel(module);
        manager = new GraphManager(project, module, file, editor);
        editor.addGraphChangeListener(manager);
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
                return DesignerEditor.this;
            }

            @Override
            public int compareTo(@NotNull FileEditorLocation o) {
                return 0;
            }
        };
    }

    @Override
    public void dispose() {
        this.manager.dispose();
    }

}
