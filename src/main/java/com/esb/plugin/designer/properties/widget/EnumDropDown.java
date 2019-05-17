package com.esb.plugin.designer.properties.widget;

import com.esb.plugin.designer.properties.InputChangeListener;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

public class EnumDropDown extends JComboBox<String> implements ItemListener {

    private InputChangeListener<String> listener;

    public EnumDropDown(List<String> values) {
        super(values.toArray(new String[0]));
        getPreferredSize().width = 150;
        addItemListener(this);
    }

    @Override
    public void itemStateChanged(ItemEvent event) {
        if (event.getStateChange() == ItemEvent.SELECTED) {
            String item = (String) event.getItem();
            if (listener != null) {
                listener.onChange(item);
            }
        }
    }

    public void addListener(InputChangeListener<String> changeListener) {
        this.listener = changeListener;
    }

}
