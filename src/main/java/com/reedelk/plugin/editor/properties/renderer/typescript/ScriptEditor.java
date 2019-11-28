package com.reedelk.plugin.editor.properties.renderer.typescript;

import com.intellij.openapi.Disposable;
import com.reedelk.plugin.editor.properties.renderer.commons.DynamicValueField;

import javax.swing.*;

public interface ScriptEditor extends Disposable {

    JComponent getComponent();

    String getValue();

    void setValue(String value);

    void setListener(DynamicValueField.OnChangeListener listener);

}
