package com.reedelk.plugin.editor.properties.renderer.typescript;

import com.intellij.codeInsight.AutoPopupController;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ThreeComponentsSplitter;
import com.reedelk.plugin.editor.properties.commons.DisposablePanel;
import com.reedelk.plugin.editor.properties.renderer.commons.DynamicValueField;
import com.reedelk.plugin.editor.properties.renderer.typedynamicvalue.DynamicValueScriptEditor;
import com.reedelk.runtime.api.commons.ScriptUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

import static com.intellij.openapi.command.WriteCommandAction.writeCommandAction;
import static com.reedelk.plugin.editor.properties.renderer.commons.ScriptEditorConstants.JAVASCRIPT_FILE_TYPE;
import static java.awt.BorderLayout.CENTER;

public class ScriptEditorDefault extends DisposablePanel implements ScriptEditor, DocumentListener {

    private static final Logger LOG = Logger.getInstance(DynamicValueScriptEditor.class);

    private static final boolean HORIZONTAL = false;
    private DynamicValueField.OnChangeListener listener;

    private static class Dimensions {
        private static final int DIVIDER_WIDTH = 0;
        private static final int DIVIDER_MOUSE_ZONE_WIDTH = 4;
        private static final int EDITOR_CONTEXT_VARIABLES_SIZE = 170;
    }

    protected Project project;
    protected EditorEx editor;
    protected Document document;

    public ScriptEditorDefault(Project project, Document document, boolean showContextVariablesPanel) {

        this.project =  project;

        document.addDocumentListener(this);

        this.editor = (EditorEx) EditorFactory.getInstance()
                .createEditor(document, project, JAVASCRIPT_FILE_TYPE, false);
        this.editor.setOneLineMode(false);
        JComponent editorComponent = editor.getComponent();

        setLayout(new BorderLayout());

        // TODO: Use decorator instead.
        if (showContextVariablesPanel) {
            ThreeComponentsSplitter splitter = new ThreeComponentsSplitter(HORIZONTAL);
            splitter.setFirstComponent(new ScriptEditorContextPanel());
            splitter.setLastComponent(editorComponent);
            splitter.setDividerWidth(Dimensions.DIVIDER_WIDTH);
            splitter.setFirstSize(Dimensions.EDITOR_CONTEXT_VARIABLES_SIZE);
            splitter.setDividerMouseZoneSize(Dimensions.DIVIDER_MOUSE_ZONE_WIDTH);
            add(splitter, CENTER);

        } else {
            add(editorComponent, CENTER);
        }

    }

    @Override
    public void documentChanged(@NotNull DocumentEvent event) {
        AutoPopupController.getInstance(project).scheduleAutoPopup(editor);
        if (listener != null) {
            // Notify when script mode changed
            String script = ScriptUtils.asScript(event.getDocument().getText());
            listener.onChange(script);
        }
    }

    @Override
    public JComponent getComponent() {
        return editor.getComponent();
    }

    @Override
    public void setListener(DynamicValueField.OnChangeListener listener) {
        this.listener = listener;
    }

    @Override
    public void setValue(String value) {
        if (value == null) return;
        try {
            String script = ScriptUtils.unwrap(value);
            writeCommandAction(project).run(() -> document.setText(script));
        } catch (Throwable throwable) {
            LOG.error(String.format("Could not write value [%s] to document", value), throwable);
        }
    }

    @Override
    public String getValue() {
        return document.getText();
    }

    @Override
    public void dispose() {
        Editor[] allEditors = EditorFactory.getInstance().getAllEditors();
        for (Editor currentEditor : allEditors) {
            if (currentEditor == editor) {
                EditorFactory.getInstance().releaseEditor(currentEditor);
            }
        }
    }
}
