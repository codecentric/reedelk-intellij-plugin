package com.reedelk.plugin.editor.properties.renderer.typescript;

import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.ListCellRendererWrapper;
import com.reedelk.plugin.commons.Labels;
import com.reedelk.plugin.editor.properties.renderer.commons.InputChangeListener;
import com.reedelk.plugin.service.module.impl.ScriptResource;
import com.reedelk.runtime.api.commons.StringUtils;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

class ScriptSelectorCombo extends ComboBox<ScriptResource> implements ItemListener {

    static final ScriptResource UNSELECTED = new ScriptResource(StringUtils.EMPTY);

    private InputChangeListener listener;

    ScriptSelectorCombo(List<ScriptResource> scripts) {
        DefaultComboBoxModel<ScriptResource> comboModel = new DefaultComboBoxModel<>();
        comboModel.addElement(UNSELECTED);
        scripts.forEach(comboModel::addElement);

        setModel(comboModel);
        setRenderer(new ScriptSelectorRenderer());
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

    private class ScriptSelectorRenderer extends ListCellRendererWrapper<ScriptResource> {
        @Override
        public void customize(JList list, ScriptResource value, int index, boolean selected, boolean hasFocus) {
            if (value == UNSELECTED) {
                setText(Labels.SCRIPT_NOT_SELECTED_ITEM);
            } else {
                setText(value.getPath());
            }
        }
    }
}
