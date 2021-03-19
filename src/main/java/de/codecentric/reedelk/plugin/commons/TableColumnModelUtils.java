package de.codecentric.reedelk.plugin.commons;

import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.util.Enumeration;

public class TableColumnModelUtils {

    private TableColumnModelUtils() {
    }

    public static void onCommit(TableColumnModel tableColumnModel) {
        Enumeration<TableColumn> columns = tableColumnModel.getColumns();
        while (columns.hasMoreElements()) {
            onCommit(columns.nextElement());
        }
    }

    private static void onCommit(TableColumn tableColumn) {
        if (tableColumn != null) {
            TableCellEditor cellEditor = tableColumn.getCellEditor();
            if (cellEditor != null) {
                cellEditor.stopCellEditing();
            }
        }
    }
}
