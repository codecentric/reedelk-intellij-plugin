package com.esb.plugin.editor;

import com.esb.plugin.editor.designer.DesignerPanelActionHandler;
import com.esb.plugin.editor.designer.FlowDesignerPanel;
import com.esb.plugin.editor.designer.ScrollableDesignerPanel;
import com.esb.plugin.graph.FlowSnapshot;
import com.esb.plugin.graph.manager.GraphManager;
import com.intellij.codeHighlighting.BackgroundEditorHighlighter;
import com.intellij.codeHighlighting.HighlightingPass;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorLocation;
import com.intellij.openapi.fileEditor.FileEditorState;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.PossiblyDumbAware;
import com.intellij.openapi.util.UserDataHolderBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.beans.PropertyChangeListener;

public class FlowDesignerEditor extends UserDataHolderBase implements FileEditor, PossiblyDumbAware, DocumentListener {

    private GraphManager manager;
    private JComponent editor;

    FlowDesignerEditor(Module module, FlowSnapshot snapshot, GraphManager manager, DesignerPanelActionHandler actionHandler) {
        this.manager = manager;

        FlowDesignerPanel flowDesignerPanel = new FlowDesignerPanel(module, snapshot, actionHandler);
        this.editor = new ScrollableDesignerPanel(flowDesignerPanel);
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
        // No op
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
        // No op
    }

    @Override
    public void deselectNotify() {
        // No op
    }

    @Override
    public void addPropertyChangeListener(@NotNull PropertyChangeListener listener) {
        // No op
    }

    @Override
    public void removePropertyChangeListener(@NotNull PropertyChangeListener listener) {
        // No op
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
                return FlowDesignerEditor.this;
            }

            @Override
            public int compareTo(@NotNull FileEditorLocation o) {
                return 0;
            }
        };
    }

    @Override
    public void dispose() {
        manager.dispose();
    }

}
