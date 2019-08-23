package com.reedelk.plugin.editor.properties.widget;

import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;
import com.reedelk.plugin.commons.Sizes;

import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

public class PropertyTable extends JBScrollPane {

    private final JBTable table;
    private final PropertyTableModel tableModel;

    public PropertyTable(PropertyTableModel tableModel, TableColumnModel tableColumnModel) {
        setPreferredSize(Sizes.Table.HEIGHT);

        this.tableModel = tableModel;

        table = new JBTable(tableModel, tableColumnModel);
        table.setFillsViewportHeight(true);
        table.setRowHeight(Sizes.Table.ROW_HEIGHT);
        setViewportView(table);
    }

    public JBTable getTable() {
        return table;
    }

    public void addEmptyRow() {
        tableModel.addRow(new Object[]{});
    }

    public void removeSelectedRow() {
        int selectedRow = table.getSelectedRow();

        // If the selected row is -1, it means that we called remove,
        // without having selected any item in the table.
        if (selectedRow > -1) {
            tableModel.removeRow(selectedRow);

            // Shift the selection to previous/next item.
            if (table.getModel().getRowCount() > 0) {
                if (table.getModel().getRowCount() >= selectedRow + 1) {
                    // Existed one after the one removed.
                    table.setRowSelectionInterval(selectedRow, selectedRow);
                } else {
                    // It did not exist a row after the one we removed.
                    table.setRowSelectionInterval(selectedRow - 1, selectedRow - 1);
                }
            }
        }
    }

    public interface PropertyTableModel extends TableModel {

        default void addRow(Object[] rowData) {
            throw new UnsupportedOperationException();
        }

        default void removeRow(int row) {
            throw new UnsupportedOperationException();
        }
    }
}
