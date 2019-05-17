package com.esb.plugin.designer.properties.widget;

import com.esb.plugin.designer.properties.InputChangeListener;
import com.intellij.ui.components.JBTextField;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class StringInputField extends JBTextField implements DocumentListener {

    private InputChangeListener<String> listener;

    public StringInputField() {
        getDocument().addDocumentListener(this);
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        if (listener != null) {
            listener.onChange(StringInputField.this.getText());
        }
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        if (listener != null) {
            listener.onChange(StringInputField.this.getText());
        }
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        if (listener != null) {
            listener.onChange(StringInputField.this.getText());
        }
    }

    public void addListener(InputChangeListener<String> changeListener) {
        this.listener = changeListener;
    }

}
