package com.reedelk.plugin.editor.properties.commons;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.table.JBTable;
import com.intellij.util.messages.MessageBusConnection;
import com.reedelk.plugin.commons.DisposableUtils;
import com.reedelk.plugin.commons.Sizes;
import com.reedelk.plugin.commons.TableColumnModelUtils;
import com.reedelk.plugin.commons.Topics;
import com.reedelk.plugin.editor.properties.CommitPropertiesListener;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.table.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Collections;
import java.util.Enumeration;

import static com.intellij.util.ui.JBUI.Borders.customLine;
import static com.intellij.util.ui.JBUI.Borders.emptyLeft;

public class DisposableTable extends JBTable implements Disposable, CommitPropertiesListener {

    private final transient MessageBusConnection busConnection;

    public DisposableTable(@NotNull Project project,
                           @NotNull TableModel tableModel,
                           boolean fillViewPortHeight) {
        super(tableModel);
        addFocusListener(new ClearSelectionFocusListener());
        setRowHeight(Sizes.Table.ROW_HEIGHT);
        setFillsViewportHeight(fillViewPortHeight);

        configureHeaderRenderer();

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

    private void configureHeaderRenderer() {
        JTableHeader tableHeader = getTableHeader();
        tableHeader.setOpaque(false);
        tableHeader.setDefaultRenderer((table, value, isSelected, hasFocus, row, column) -> {
            JBLabel headerLabel = new JBLabel((String) value);
            headerLabel.setOpaque(true);
            Border border = emptyLeft(6);
            // The first header has a border on the right side of the label to mark
            // the end of the header content.
            Border customLine = column == 0 ?
                    customLine(JBColor.LIGHT_GRAY, 0, 0, 1, 1) :
                    customLine(JBColor.LIGHT_GRAY, 0, 0, 1, 0);
            headerLabel.setBorder(new CompoundBorder(customLine, border));
            return headerLabel;
        });
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
