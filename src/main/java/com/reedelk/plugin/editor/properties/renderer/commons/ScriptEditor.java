package com.reedelk.plugin.editor.properties.renderer.commons;

import com.intellij.codeInsight.AutoPopupController;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.module.Module;
import com.reedelk.plugin.editor.properties.commons.DisposablePanel;
import com.reedelk.runtime.api.commons.ScriptUtils;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

import static com.intellij.openapi.command.WriteCommandAction.writeCommandAction;
import static com.reedelk.plugin.editor.properties.renderer.commons.ScriptEditorConstants.JAVASCRIPT_FILE_TYPE;
import static java.awt.BorderLayout.CENTER;

public class ScriptEditor extends DisposablePanel implements DocumentListener {

    private static final Logger LOG = Logger.getInstance(ScriptEditor.class);

    private transient final Module module;
    private transient final EditorEx editor;
    private transient final Document document;
    private transient ScriptEditorChangeListener listener;


    public ScriptEditor(@NotNull Module module, @NotNull Document document) {
        this.module = module;
        this.document = document;
        this.editor = (EditorEx) EditorFactory.getInstance()
                .createEditor(document, module.getProject(), JAVASCRIPT_FILE_TYPE, false);

        configure(this.editor);

        document.addDocumentListener(this);
        setLayout(new BorderLayout());
        add(editor.getComponent(), CENTER);
    }

    @Override
    public void documentChanged(@NotNull DocumentEvent event) {
        AutoPopupController.getInstance(module.getProject()).scheduleAutoPopup(editor);
        if (listener != null) {
            // Notify when script mode changed
            String script = ScriptUtils.asScript(event.getDocument().getText());
            listener.onChange(script);
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        Editor[] allEditors = EditorFactory.getInstance().getAllEditors();
        for (Editor currentEditor : allEditors) {
            if (currentEditor == editor) {
                EditorFactory.getInstance().releaseEditor(currentEditor);
            }
        }
    }

    public String getValue() {
        return document.getText();
    }

    public void setValue(String value) {
        if (value == null) return; // TODO: Do I need this??!? What  if  value is   empty value!? check it
        try {
            String script = ScriptUtils.unwrap(value);
            writeCommandAction(module.getProject()).run(() -> document.setText(script));
        } catch (Throwable throwable) {
            LOG.error(String.format("Could not write value [%s] to document", value), throwable);
        }
    }

    public void setListener(ScriptEditorChangeListener listener) {
        this.listener = listener;
    }

    protected void configure(EditorEx editor) {
    }
}