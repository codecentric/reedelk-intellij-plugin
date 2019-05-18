package com.esb.plugin.designer.properties.widget.input;

import com.esb.plugin.converter.ValueConverter;
import com.esb.plugin.converter.ValueConverterFactory;
import com.intellij.ui.components.JBCheckBox;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BooleanCheckbox extends JBCheckBox implements ActionListener {

    private JBCheckBox checkBox;
    private InputChangeListener<Boolean> listener;
    private ValueConverter<Boolean> converter;

    public BooleanCheckbox() {
        checkBox = new JBCheckBox();
        checkBox.addActionListener(this);
        converter = ValueConverterFactory.forType(boolean.class);
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

    public void setValue(Object value) {
        String valueAsString = converter.toText(value);
        checkBox.setSelected(Boolean.parseBoolean(valueAsString));
    }

    public void addListener(InputChangeListener<Boolean> listener) {
        this.listener = listener;
    }
}