package com.reedelk.plugin.editor.properties.commons;

import com.intellij.openapi.project.Project;
import com.intellij.ui.table.JBTable;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class DisposableScrollableTable extends DisposableScrollPane {

    private final transient JBTable table;
    private final transient DisposableTableModel tableModel;

    public DisposableScrollableTable(@NotNull Project project,
                                     @NotNull Dimension preferredSize,
                                     @NotNull DisposableTableModel tableModel,
                                     @NotNull DisposableTableColumnModelFactory columnModelFactory) {
        this.tableModel = tableModel;
        this.table = new DisposableTable(project, tableModel, true);

        setPreferredSize(preferredSize);
        setViewportView(this.table);

        columnModelFactory.create(this.table);
    }

    public void addEmptyRow() {
        tableModel.addRow(tableModel.createRow());
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
}
