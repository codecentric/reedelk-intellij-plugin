package com.reedelk.plugin.editor.properties.widget.input.script;

import com.intellij.openapi.ui.ComboBox;
import com.reedelk.plugin.editor.properties.widget.input.InputChangeListener;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

public class ScriptSelector extends ComboBox<String> implements ItemListener {

    private InputChangeListener listener;

    public ScriptSelector(List<String> scripts) {
        DefaultComboBoxModel<String> comboModel = new DefaultComboBoxModel<>();
        scripts.forEach(comboModel::addElement);
        setModel(comboModel);
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
}
