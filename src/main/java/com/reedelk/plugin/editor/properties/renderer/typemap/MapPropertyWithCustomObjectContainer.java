package com.reedelk.plugin.editor.properties.renderer.typemap;

import com.intellij.openapi.ui.ComboBox;
import com.reedelk.plugin.editor.properties.commons.DisposablePanel;

import javax.swing.*;
import java.awt.*;

public class MapPropertyWithCustomObjectContainer extends DisposablePanel {

    static final String[] COLUMN_NAMES = {"Key", "Edit"};

    static class ListItem {
        String displayName;

    }
    public MapPropertyWithCustomObjectContainer() {
        setLayout(new BorderLayout());

        ListItem one = new ListItem();
        one.displayName = "200";
        ListItem two = new ListItem();
        two.displayName = "401";



        DefaultComboBoxModel<ListItem> comboBoxModel = new DefaultComboBoxModel<>();
        comboBoxModel.addElement(one);
        comboBoxModel.addElement(two);

        ComboBox<ListItem> combo = new ComboBox<>(comboBoxModel);

        combo.setRenderer((list, value, index, isSelected, cellHasFocus) -> new JLabel(value.displayName));

        // The key is the name of the item in the list

        add(combo, BorderLayout.WEST);
    }
}
