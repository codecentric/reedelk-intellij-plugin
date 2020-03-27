package com.reedelk.plugin.editor.properties.renderer.typeboolean;

import com.intellij.ui.components.JBCheckBox;
import com.reedelk.plugin.editor.properties.commons.InputChangeListener;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BooleanCheckbox extends JBCheckBox implements ActionListener {

    private transient InputChangeListener listener;

    public BooleanCheckbox() {
        addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JCheckBox checkBox = (JCheckBox) e.getSource();
        listener.onChange(checkBox.isSelected());
    }

    public void setValue(Boolean value) {
        setSelected(value == null ? false : value);
    }

    public boolean getValue() {
        return isSelected();
    }

    public void addListener(InputChangeListener listener) {
        this.listener = listener;
    }
}
