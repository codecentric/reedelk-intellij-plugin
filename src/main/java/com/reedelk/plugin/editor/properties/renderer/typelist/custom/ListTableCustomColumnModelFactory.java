package com.reedelk.plugin.editor.properties.renderer.typelist.custom;

import com.intellij.ui.table.JBTable;
import com.reedelk.plugin.editor.properties.commons.DisposableTableColumnModelFactory;
import com.reedelk.plugin.editor.properties.commons.TableEditButtonCellEditor;

import javax.swing.table.TableColumn;

import static com.reedelk.plugin.editor.properties.commons.TableEditButtonCellEditor.TableCustomEditButtonAction;

public class ListTableCustomColumnModelFactory implements DisposableTableColumnModelFactory {

    private static final int EDIT_COLUMN_WIDTH = 150;

    private final TableCustomEditButtonAction action;
    private final ListTableItemDisplayCellRenderer cellRenderer;

    public ListTableCustomColumnModelFactory(TableCustomEditButtonAction action, String listDisplayPropertyName) {
        this.action = action;
        this.cellRenderer = new ListTableItemDisplayCellRenderer(listDisplayPropertyName);
    }

    @Override
    public void create(JBTable table) {
        // Column 1 (the map key)
        TableColumn keyColumn = table.getColumnModel().getColumn(0);
        keyColumn.setCellRenderer(cellRenderer);

        TableEditButtonCellEditor editButtonColumn = new TableEditButtonCellEditor(table, action);

        // Column 2 (the map value)
        TableColumn valueColumn = table.getColumnModel().getColumn(1);
        valueColumn.setCellRenderer(editButtonColumn);
        valueColumn.setCellEditor(editButtonColumn);
        valueColumn.setPreferredWidth(EDIT_COLUMN_WIDTH);
        valueColumn.setMaxWidth(EDIT_COLUMN_WIDTH);

        // It is a list and not a map, we don't show the table header.
        table.setTableHeader(null);
    }
}
