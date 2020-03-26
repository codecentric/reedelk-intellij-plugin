package com.reedelk.plugin.editor.properties.renderer.typelist;

import com.intellij.util.ui.UIUtil;

import javax.swing.*;
import java.awt.*;

public class StripedItemCellRenderer extends DefaultListCellRenderer {

    public StripedItemCellRenderer() {
        setOpaque(true);
    }

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (!isSelected) {
            component.setBackground(index % 2 == 0 ? UIUtil.getDecoratedRowColor() : UIUtil.getTableBackground());
            component.setForeground(UIUtil.getTextFieldForeground());
        }
        return this;
    }
}
