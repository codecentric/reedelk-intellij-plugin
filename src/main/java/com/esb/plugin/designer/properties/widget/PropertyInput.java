package com.esb.plugin.designer.properties.widget;

import com.esb.plugin.designer.properties.InputChangeListener;
import com.intellij.ui.components.JBTextField;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public class PropertyInput extends JBTextField implements DocumentListener {

    private InputChangeListener changeListener;

    public PropertyInput() {
        setMaximumSize(new Dimension(300, 50));
        setAlignmentX(LEFT_ALIGNMENT);
        getDocument().addDocumentListener(this);
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        if (changeListener != null) {
            changeListener.onChange(PropertyInput.this.getText());
        }
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        if (changeListener != null) {
            changeListener.onChange(PropertyInput.this.getText());
        }
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        if (changeListener != null) {
            changeListener.onChange(PropertyInput.this.getText());
        }
    }

    public void addListener(InputChangeListener changeListener) {
        this.changeListener = changeListener;
    }
}
