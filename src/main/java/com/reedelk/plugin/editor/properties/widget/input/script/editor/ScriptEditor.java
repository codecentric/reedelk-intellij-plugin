package com.reedelk.plugin.editor.properties.widget.input.script.editor;

import com.intellij.openapi.Disposable;
import com.reedelk.plugin.editor.properties.widget.input.script.DynamicValueField;

import javax.swing.*;

import static com.reedelk.plugin.editor.properties.widget.input.script.suggestion.SuggestionDropDownDecorator.OnEditDoneCallback;

public interface ScriptEditor extends Disposable {

    void addOnEditDone(OnEditDoneCallback editDoneCallback);

    JComponent getComponent();

    String getValue();

    void setValue(String value);

    void setListener(DynamicValueField.OnChangeListener listener);

}
