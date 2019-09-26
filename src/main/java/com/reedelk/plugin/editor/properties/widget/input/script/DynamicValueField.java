package com.reedelk.plugin.editor.properties.widget.input.script;

import com.intellij.openapi.module.Module;
import com.intellij.util.ui.JBUI;
import com.reedelk.plugin.editor.properties.widget.DisposablePanel;
import com.reedelk.plugin.editor.properties.widget.input.InputChangeListener;
import com.reedelk.plugin.editor.properties.widget.input.script.editor.JavascriptDynamicValueEditor;
import com.reedelk.plugin.editor.properties.widget.input.script.editor.JavascriptEditor;

import java.awt.*;

import static java.awt.BorderLayout.CENTER;

public class DynamicValueField extends DisposablePanel {

    private JavascriptEditor editor;

    public DynamicValueField(Module module, ScriptContextManager context, String hint) {
        editor = new JavascriptDynamicValueEditor(module.getProject(), context, hint);
        setLayout(new BorderLayout());
        add(editor.getComponent(), CENTER);
        setBorder(JBUI.Borders.empty(0, 3));
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