package com.reedelk.plugin.editor.properties.widget.input;

import com.intellij.openapi.ui.ComboBox;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Arrays;

import static java.util.Arrays.stream;

public class StringDropDown extends ComboBox<String> implements ItemListener {

    private InputChangeListener listener;

    public StringDropDown(String[] items, boolean editable) {
        Arrays.sort(items); // sort ascending order

        DefaultComboBoxModel<String> comboModel = new DefaultComboBoxModel<>();
        stream(items).forEach(comboModel::addElement);
        setModel(comboModel);
        setEditable(editable);
        addItemListener(this);
    }

    @Override
    public void itemStateChanged(ItemEvent event) {
        if (event.getStateChange() == ItemEvent.SELECTED) {
            if (listener != null) {
                String item = (String) event.getItem();
                listener.onChange(item);
            }
        }
    }

    public void addListener(InputChangeListener changeListener) {
        this.listener = changeListener;
    }

    public void setValue(Object value) {
        setSelectedItem(value);
    }
}
