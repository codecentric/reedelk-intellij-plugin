package com.reedelk.plugin.editor.properties.renderer.commons;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class DynamicValueInputFieldBaseAdapter<T> implements DynamicValueInputFieldAdapter {

    private final InputField<T> inputField;

    public DynamicValueInputFieldBaseAdapter(InputField<T> inputField) {
        this.inputField = inputField;
    }

    @Override
    public Object getValue() {
        return inputField.getValue();
    }

    @Override
    public JComponent getComponent() {
        return inputField;
    }

    @Override
    public void setFont(Font font) {
        inputField.setFont(font);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setValue(Object value) {
        inputField.setValue((T) value);
    }

    @Override
    public void setMargin(Insets insets) {
        inputField.setMargin(insets);
    }

    @Override
    public void setBorder(Border border) {
        inputField.setBorder(border);
    }

    @Override
    public void addListener(ScriptEditorChangeListener listener) {
        inputField.addListener(listener::onChange);
    }
}
