package com.esb.plugin.designer.editor.properties;

import com.intellij.ui.components.JBTextField;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public class PropertyInput extends JBTextField {

    private InputChangeListener changeListener;

    PropertyInput() {
        setMaximumSize(new Dimension(300, 50));
        setAlignmentX(Component.LEFT_ALIGNMENT);
        getDocument().addDocumentListener(new PropertyInputDocumentListener());
    }

    public void addListener(InputChangeListener changeListener) {
        this.changeListener = changeListener;
    }

    class PropertyInputDocumentListener implements DocumentListener {

        @Override
        public void insertUpdate(DocumentEvent e) {
            changeListener.onChange(PropertyInput.this.getText());
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            changeListener.onChange(PropertyInput.this.getText());
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            changeListener.onChange(PropertyInput.this.getText());
        }
    }
}
