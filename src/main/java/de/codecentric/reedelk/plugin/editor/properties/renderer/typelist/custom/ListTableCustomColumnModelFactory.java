package de.codecentric.reedelk.plugin.editor.properties.renderer.typelist.custom;

import de.codecentric.reedelk.plugin.editor.properties.commons.DisposableTableColumnModelFactory;
import de.codecentric.reedelk.plugin.editor.properties.commons.TableEditButtonCellEditor;
import com.intellij.ui.table.JBTable;

import javax.swing.table.TableColumn;

public class ListTableCustomColumnModelFactory implements DisposableTableColumnModelFactory {

    private static final int EDIT_COLUMN_WIDTH = 150;

    private final TableEditButtonCellEditor.TableCustomEditButtonAction action;
    private final ListTableItemDisplayCellRenderer cellRenderer;

    public ListTableCustomColumnModelFactory(TableEditButtonCellEditor.TableCustomEditButtonAction action, String listDisplayPropertyName) {
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
