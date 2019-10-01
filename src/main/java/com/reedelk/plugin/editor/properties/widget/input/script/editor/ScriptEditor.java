package com.reedelk.plugin.editor.properties.widget.input.script.editor;

import com.intellij.openapi.Disposable;
import com.reedelk.plugin.editor.properties.widget.input.script.DynamicValueField;

import javax.swing.*;

public interface ScriptEditor extends Disposable {

    JComponent getComponent();

    String getValue();

    void setValue(String value);

    void setListener(DynamicValueField.OnChangeListener listener);

}
