package com.reedelk.plugin.editor.properties.widget.input.script;

import com.intellij.openapi.ui.ComboBox;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

public class ScriptSelector extends ComboBox<String> implements ItemListener {

    public ScriptSelector(List<String> scripts) {
        DefaultComboBoxModel<String> comboModel = new DefaultComboBoxModel<>();
        scripts.forEach(comboModel::addElement);
        setModel(comboModel);
    }

    @Override
    public void itemStateChanged(ItemEvent e) {

    }
}
