package de.codecentric.reedelk.plugin.editor;

import de.codecentric.reedelk.plugin.graph.FlowSnapshot;
import de.codecentric.reedelk.plugin.graph.manager.GraphManager;
import com.intellij.codeHighlighting.BackgroundEditorHighlighter;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorLocation;
import com.intellij.openapi.fileEditor.FileEditorState;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.util.UserDataHolderBase;
import de.codecentric.reedelk.plugin.editor.designer.ScrollableDesignerPanel;
import de.codecentric.reedelk.plugin.editor.designer.SubFlowDesignerPanel;
import de.codecentric.reedelk.plugin.editor.designer.dnd.DesignerPanelActionHandler;
import de.codecentric.reedelk.plugin.editor.properties.commons.DisposableScrollPane;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.beans.PropertyChangeListener;

public class SubFlowDesignerEditor extends UserDataHolderBase implements DesignerEditor {

    private GraphManager manager;
    private DisposableScrollPane editor;

    SubFlowDesignerEditor(Module module, FlowSnapshot snapshot, GraphManager manager, DesignerPanelActionHandler actionHandler) {
        this.manager = manager;
        SubFlowDesignerPanel subFlowDesignerPanel = new SubFlowDesignerPanel(module, snapshot, actionHandler);
        this.editor = new ScrollableDesignerPanel(subFlowDesignerPanel);
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
        return null;
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
        editor.dispose();
    }
}
