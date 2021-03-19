package de.codecentric.reedelk.plugin.editor.properties.commons;

import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class StripedRowCellRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel component = (StripedRowCellRenderer) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        component.setBorder(JBUI.Borders.emptyLeft(5));
        if (!isSelected) {
            component.setBackground(row % 2 != 0 ? UIUtil.getDecoratedRowColor() : UIUtil.getTableBackground());
            component.setForeground(UIUtil.getTextFieldForeground());
        }
        return component;
    }
}
