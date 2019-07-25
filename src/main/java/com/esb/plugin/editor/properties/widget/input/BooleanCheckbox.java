package com.esb.plugin.editor.properties.widget.input;

import com.esb.plugin.commons.Colors;
import com.esb.plugin.converter.BooleanConverter;
import com.esb.plugin.converter.ValueConverter;
import com.intellij.ui.components.JBCheckBox;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BooleanCheckbox extends JBCheckBox implements ActionListener {

    private final ValueConverter<Boolean> converter = new BooleanConverter();

    private InputChangeListener<Boolean> listener;

    public BooleanCheckbox() {
        setBackground(Colors.PROPERTIES_BACKGROUND);
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
    public void setValue(Object value) {
        String valueAsString = converter.toText(value);
        setSelected(Boolean.parseBoolean(valueAsString));
    }

    public void addListener(InputChangeListener<Boolean> listener) {
        this.listener = listener;
    }
}
