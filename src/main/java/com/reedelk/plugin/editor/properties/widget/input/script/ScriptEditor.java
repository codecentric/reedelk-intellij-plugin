package com.reedelk.plugin.editor.properties.widget.input.script;

import com.intellij.openapi.Disposable;

import javax.swing.*;

public interface ScriptEditor extends Disposable {

    JComponent getComponent();

    String getValue();

    void setValue(String value);

    void setListener(DynamicValueField.OnChangeListener listener);

}
