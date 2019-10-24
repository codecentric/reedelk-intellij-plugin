package com.reedelk.plugin.editor.properties.widget.input;

import com.reedelk.plugin.editor.properties.widget.ComboDropdownSuggestion;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class StringDropDown extends JComboBox<String> implements ItemListener {

    private InputChangeListener listener;

    public StringDropDown(String[] items, boolean editable, String prototype) {
        super(items);
        setEditable(editable);
        if (editable) {
            JTextField field = (JTextField) getEditor().getEditorComponent();
            field.addKeyListener(new ComboDropdownSuggestion(this));
        }
        if (prototype != null) {
            setPrototypeDisplayValue(prototype);
        }
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
