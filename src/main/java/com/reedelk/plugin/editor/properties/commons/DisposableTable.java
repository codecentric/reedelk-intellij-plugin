package com.reedelk.plugin.editor.properties.commons;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.ui.table.JBTable;
import com.intellij.util.messages.MessageBusConnection;
import com.reedelk.plugin.commons.DisposableUtils;
import com.reedelk.plugin.commons.Sizes;
import com.reedelk.plugin.commons.TableColumnModelUtils;
import com.reedelk.plugin.commons.Topics;
import com.reedelk.plugin.editor.properties.CommitPropertiesListener;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Collections;
import java.util.Enumeration;

public class DisposableTable extends JBTable implements Disposable, CommitPropertiesListener {

    private final transient MessageBusConnection busConnection;

    public DisposableTable(@NotNull Project project,
                           @NotNull TableModel tableModel,
                           boolean fillViewPortHeight) {
        super(tableModel);
        addFocusListener(new ClearSelectionFocusListener());
        setRowHeight(Sizes.Table.ROW_HEIGHT);
        setFillsViewportHeight(fillViewPortHeight);
        this.busConnection = project.getMessageBus().connect();
        this.busConnection.subscribe(Topics.COMMIT_COMPONENT_PROPERTIES_EVENTS, this);
    }

    @Override
    public void dispose() {
        this.busConnection.disconnect();

        if (getModel() != null) {
            TableModel tableModel = getModel();
            if (tableModel instanceof Disposable) {
                ((Disposable) tableModel).dispose();
            }
        }

        if (getColumnModel() != null) {
            Enumeration<TableColumn> columns = getColumnModel().getColumns();
            Collections.list(columns).forEach(tableColumn -> {
                TableCellEditor cellEditor = tableColumn.getCellEditor();
                TableCellRenderer cellRenderer = tableColumn.getCellRenderer();
                DisposableUtils.dispose(cellEditor);
                DisposableUtils.dispose(cellRenderer);
            });
        }
    }

    @Override
    public void onCommit() {
        TableColumnModelUtils.onCommit(getColumnModel());
    }

    private class ClearSelectionFocusListener implements FocusListener {

        @Override
        public void focusGained(FocusEvent event) {
            // nothing to do when focus is gained
        }

        @Override
        public void focusLost(FocusEvent event) {
            ListSelectionModel selectionModel = getSelectionModel();
            if (selectionModel != null) {
                selectionModel.clearSelection();
            }
        }
    }
}
