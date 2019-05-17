package com.esb.plugin.designer.properties.widget.input;

import com.esb.plugin.converter.ValueConverter;
import com.esb.plugin.converter.ValueConverterFactory;
import com.intellij.ui.components.JBCheckBox;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.WEST;

public class BooleanCheckbox extends JPanel implements ActionListener {

    private JBCheckBox checkBox;
    private InputChangeListener<Boolean> listener;
    private ValueConverter<Boolean> converter;

    public BooleanCheckbox() {
        checkBox = new JBCheckBox();
        add(checkBox, WEST);
        add(Box.createHorizontalBox(), CENTER);
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
        String valueAsString = converter.toString(value);
        checkBox.setSelected(Boolean.parseBoolean(valueAsString));
    }

    public void addListener(InputChangeListener<Boolean> listener) {
        this.listener = listener;
    }
}
