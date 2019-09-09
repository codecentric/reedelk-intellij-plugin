package com.reedelk.plugin.editor.properties.widget.input.script;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.editor.properties.widget.DisposablePanel;
import com.reedelk.plugin.editor.properties.widget.input.InputChangeListener;
import com.reedelk.plugin.editor.properties.widget.input.script.editor.JavascriptEditor;
import com.reedelk.plugin.editor.properties.widget.input.script.editor.JavascriptEditorInline;

import java.awt.*;

import static java.awt.BorderLayout.CENTER;

public class ScriptInputInlineField extends DisposablePanel {

    private JavascriptEditor editor;

    public ScriptInputInlineField(Module module, ScriptContextManager context, String hint) {
        editor = new JavascriptEditorInline(module.getProject(), context, hint);
        setLayout(new BorderLayout());
        add(editor.getComponent(), CENTER);
    }

    public String getValue() {
        return editor.getValue();
    }

    public void setValue(String newValue) {
        editor.setValue(newValue);
    }

    public void addListener(InputChangeListener<String> listener) {
        this.editor.addListener(listener);
    }
}