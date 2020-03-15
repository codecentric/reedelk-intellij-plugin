package com.reedelk.plugin.editor.properties.renderer.typemap;

import com.intellij.ui.components.JBTextField;
import com.intellij.ui.table.JBTable;
import com.reedelk.plugin.editor.properties.commons.DisposableTableColumnModelFactory;

import javax.swing.*;
import javax.swing.table.TableColumn;

public class MapTableColumnModel implements DisposableTableColumnModelFactory {

    private static final String[] COLUMN_NAMES = {"Key", "Value"};

    @Override
    public void create(JBTable table) {
        // Column 1 (the map key)
        TableColumn keyColumn = table.getColumnModel().getColumn(0);
        keyColumn.setHeaderValue(COLUMN_NAMES[0]);
        keyColumn.setCellEditor(new DefaultCellEditor(new JBTextField()));
        table.addColumn(keyColumn);

        // Column 2 (the map value)
        TableColumn valueColumn = table.getColumnModel().getColumn(1);
        valueColumn.setHeaderValue(COLUMN_NAMES[1]);
        valueColumn.setCellEditor(new DefaultCellEditor(new JBTextField()));
        table.addColumn(valueColumn);
    }
}
