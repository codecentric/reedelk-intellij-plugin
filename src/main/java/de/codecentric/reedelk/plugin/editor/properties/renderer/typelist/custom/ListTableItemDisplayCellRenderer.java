package de.codecentric.reedelk.plugin.editor.properties.renderer.typelist.custom;

import de.codecentric.reedelk.plugin.editor.properties.commons.StripedRowCellRenderer;
import de.codecentric.reedelk.module.descriptor.model.property.ObjectDescriptor;

import javax.swing.*;
import java.awt.*;

public class ListTableItemDisplayCellRenderer extends StripedRowCellRenderer {

    private final String listDisplayPropertyName;

    public ListTableItemDisplayCellRenderer(String listDisplayPropertyName) {
        this.listDisplayPropertyName = listDisplayPropertyName;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel component = (StripedRowCellRenderer) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        component.setText(((ObjectDescriptor.TypeObject) value).get(listDisplayPropertyName));
        return component;
    }
}
