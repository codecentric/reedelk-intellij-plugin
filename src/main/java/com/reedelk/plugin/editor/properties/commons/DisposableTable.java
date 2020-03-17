package com.reedelk.plugin.editor.properties.commons;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.ui.table.JBTable;
import com.intellij.util.messages.MessageBusConnection;
import com.reedelk.plugin.commons.DisposableUtils;
import com.reedelk.plugin.commons.Sizes;
import com.reedelk.plugin.commons.TableColumnModelUtils;
import com.reedelk.plugin.editor.properties.CommitPropertiesListener;
import com.reedelk.plugin.topic.ReedelkTopics;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Collections;
import java.util.Enumeration;

public class DisposableTable extends DisposableScrollPane implements CommitPropertiesListener {

    private final transient JBTable table;
    private final transient DisposableTableModel tableModel;
    private final transient MessageBusConnection busConnection;

    public DisposableTable(Project project, DisposableTableModel tableModel, DisposableTableColumnModelFactory columnModelFactory) {
        this.tableModel = tableModel;

        this.table = new DisposableAwareTable(tableModel);
        this.table.addFocusListener(new ClearSelectionFocusListener());
        this.table.setRowHeight(Sizes.Table.ROW_HEIGHT);
        this.table.setFillsViewportHeight(true);

        columnModelFactory.create(table);
        setPreferredSize(Sizes.Table.HEIGHT);
        setViewportView(table);

        this.busConnection = project.getMessageBus().connect();
        this.busConnection.subscribe(ReedelkTopics.COMMIT_COMPONENT_PROPERTIES_EVENTS, this);
    }


    @Override
    public void dispose() {
        super.dispose();
        this.busConnection.disconnect();

        Enumeration<TableColumn> columns = table.getColumnModel().getColumns();
        Collections.list(columns).forEach(tableColumn -> {
            TableCellEditor cellEditor = tableColumn.getCellEditor();
            TableCellRenderer cellRenderer = tableColumn.getCellRenderer();
            DisposableUtils.dispose(cellEditor);
            DisposableUtils.dispose(cellRenderer);
        });
    }

    @Override
    public void onCommit() {
        TableColumnModelUtils.onCommit(table.getColumnModel());
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


    class ClearSelectionFocusListener implements FocusListener {

        @Override
        public void focusGained(FocusEvent e) {
            // nothing to do when focus is gained
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

    private static class DisposableAwareTable extends JBTable implements Disposable {

        DisposableAwareTable(TableModel tableModel) {
            super(tableModel);
        }

        @Override
        public void dispose() {
            if (getModel() != null) {
                TableModel tableModel = getModel();
                if (tableModel instanceof Disposable) {
                    ((Disposable) tableModel).dispose();
                }
            }
            if (getColumnModel() != null) {
                TableColumnModel columnModel = getColumnModel();
                if (columnModel instanceof Disposable) {
                    ((Disposable) columnModel).dispose();
                }
            }
        }
    }
}
