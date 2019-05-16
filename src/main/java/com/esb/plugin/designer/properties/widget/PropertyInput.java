package com.esb.plugin.designer.properties.widget;

import com.esb.plugin.designer.properties.InputChangeListener;
import com.intellij.ui.components.JBTextField;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class PropertyInput extends JBTextField implements DocumentListener {

    private InputChangeListener listener;

    public PropertyInput() {
        getDocument().addDocumentListener(this);
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        if (listener != null) {
            listener.onChange(PropertyInput.this.getText());
        }
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        if (listener != null) {
            listener.onChange(PropertyInput.this.getText());
        }
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        if (listener != null) {
            listener.onChange(PropertyInput.this.getText());
        }
    }

    public void addListener(InputChangeListener changeListener) {
        this.listener = changeListener;
    }
}
