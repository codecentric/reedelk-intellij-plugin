package com.reedelk.plugin.editor.properties.commons;

import com.intellij.ui.table.JBTable;
import com.reedelk.plugin.commons.Sizes;

import javax.swing.*;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class PropertyTable extends DisposableScrollPane {

    private final transient JBTable table;
    private final transient PropertyTableModel tableModel;

    public PropertyTable(PropertyTableModel tableModel, TableColumnModel tableColumnModel) {
        setPreferredSize(Sizes.Table.HEIGHT);

        this.tableModel = tableModel;

        table = new DisposableTable(tableModel, tableColumnModel);
        table.setFillsViewportHeight(true);
        table.setRowHeight(Sizes.Table.ROW_HEIGHT);
        table.addFocusListener(new ClearSelectionFocusListener());
        setViewportView(table);
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


    class ClearSelectionFocusListener implements FocusListener {

        @Override
        public void focusGained(FocusEvent e) {

        }

        @Override
        public void focusLost(FocusEvent e) {
            if (table != null) {
                ListSelectionModel selectionModel = table.getSelectionModel();
                if (selectionModel != null) {
                    selectionModel.clearSelection();
                }
            }
        }
    }
}
