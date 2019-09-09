package com.reedelk.plugin.editor.properties.widget.input.script.editor;

import com.intellij.openapi.Disposable;
import com.reedelk.plugin.editor.properties.widget.input.InputChangeListener;

import javax.swing.*;

public interface JavascriptEditor extends Disposable {

    String getValue();

    void setValue(String value);

    void addListener(InputChangeListener<String> listener);

    JComponent getComponent();

}
