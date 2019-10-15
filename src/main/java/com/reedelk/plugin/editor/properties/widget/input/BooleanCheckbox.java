package com.reedelk.plugin.editor.properties.widget.input;

import com.intellij.ui.components.JBCheckBox;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BooleanCheckbox extends JBCheckBox implements ActionListener {

    private InputChangeListener listener;

    public BooleanCheckbox() {
        addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JCheckBox checkBox = (JCheckBox) e.getSource();
        if (checkBox.isSelected()) {
            listener.onChange(true);
        } else {
            listener.onChange(false);
        }
    }

    public void setValue(Boolean value) {
        setSelected(value);
    }

    public boolean getValue() {
        return isSelected();
    }

    public void addListener(InputChangeListener listener) {
        this.listener = listener;
    }
}