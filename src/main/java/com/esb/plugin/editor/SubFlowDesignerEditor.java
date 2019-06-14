package com.esb.plugin.editor;

import com.esb.plugin.commons.Icons;
import com.esb.plugin.commons.Labels;
import com.esb.plugin.editor.designer.DesignerPanelActionHandler;
import com.esb.plugin.editor.properties.PropertiesPanel;
import com.esb.plugin.editor.properties.ScrollablePropertiesPanel;
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

public class SubFlowDesignerEditor extends UserDataHolderBase implements FileEditor, PossiblyDumbAware, DocumentListener {

    private GraphManager manager;
    private DesignerEditor editor;

    SubFlowDesignerEditor(Module module, FlowSnapshot snapshot, GraphManager manager, DesignerPanelActionHandler actionHandler) {
        this.manager = manager;
        PropertiesPanel properties =
                new PropertiesPanel(module, snapshot, Labels.PROPERTIES_PANEL_SUBFLOW_TITLE, Icons.FileTypeSubFlow);
        ScrollablePropertiesPanel propertiesPanel =
                new ScrollablePropertiesPanel(properties);
        this.editor = new DesignerEditor(snapshot, actionHandler, propertiesPanel);
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
        return "Subflow Designer";
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
                return SubFlowDesignerEditor.this;
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