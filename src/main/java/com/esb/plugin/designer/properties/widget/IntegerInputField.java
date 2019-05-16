package com.esb.plugin.designer.properties.widget;

import com.esb.plugin.designer.properties.InputChangeListener;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.text.NumberFormat;

public class IntegerInputField extends JFormattedTextField implements DocumentListener {

    private InputChangeListener<Integer> listener;

    public IntegerInputField() {
        super(NumberFormat.getInstance());
        setFocusLostBehavior(JFormattedTextField.COMMIT);
        getDocument().addDocumentListener(this);
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        notifyListener();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        notifyListener();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        notifyListener();
    }

    public void addListener(InputChangeListener<Integer> changeListener) {
        this.listener = changeListener;
    }

    private void notifyListener() {
        if (listener != null) {
            Object value = IntegerInputField.this.getValue();
            if (value != null) {
                listener.onChange(((Long) value).intValue());
            } else {
                listener.onChange(0);
            }
        }
    }
}
