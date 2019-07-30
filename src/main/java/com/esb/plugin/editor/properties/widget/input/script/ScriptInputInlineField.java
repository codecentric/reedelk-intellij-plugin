package com.esb.plugin.editor.properties.widget.input.script;

import com.esb.plugin.editor.properties.widget.DisposablePanel;
import com.esb.plugin.editor.properties.widget.input.InputChangeListener;
import com.esb.plugin.editor.properties.widget.input.script.editor.JavascriptEditor;
import com.esb.plugin.editor.properties.widget.input.script.editor.JavascriptEditorFactory;
import com.esb.plugin.editor.properties.widget.input.script.editor.JavascriptEditorMode;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.module.Module;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class ScriptInputInlineField extends DisposablePanel implements DocumentListener {

    private InputChangeListener<String> listener;

    private JavascriptEditor editor;
    private String value = "";

    public ScriptInputInlineField(Module module, ScriptContextManager context) {
        editor = JavascriptEditorFactory.get()
                .mode(JavascriptEditorMode.INLINE)
                .project(module.getProject())
                .initialValue(value)
                .context(context)
                .build();

        editor.addDocumentListener(this);


        setLayout(new BorderLayout());
        add(editor, BorderLayout.CENTER);
    }

    @Override
    public void documentChanged(@NotNull DocumentEvent event) {
        if (listener != null) {
            listener.onChange(event.getDocument().getText());
        }
    }

    public String getValue() {
        return value;
    }

    public void setValue(String newValue) {
        this.value = newValue;
        this.editor.setValue(this.value);
    }

    public void addListener(InputChangeListener<String> listener) {
        this.listener = listener;
    }
}