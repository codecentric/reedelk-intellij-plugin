package com.reedelk.plugin.editor.properties.renderer.typemap.dynamic;

import com.intellij.openapi.module.Module;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.table.JBTable;
import com.reedelk.plugin.editor.properties.commons.ContainerContext;
import com.reedelk.plugin.editor.properties.commons.DisposableTableColumnModelFactory;
import com.reedelk.plugin.editor.properties.commons.TableDynamicCellEditor;
import com.reedelk.plugin.editor.properties.commons.TableDynamicCellRenderer;

import javax.swing.*;
import javax.swing.table.TableColumn;

public class DynamicMapTableColumnModelFactory implements DisposableTableColumnModelFactory {

    private static final String[] COLUMN_NAMES = {"Key", "Value"};

    private final Module module;
    private final ContainerContext context;

    DynamicMapTableColumnModelFactory(Module  module, ContainerContext context) {
        this.module = module;
        this.context = context;
    }

    @Override
    public void create(JBTable table) {
        TableDynamicCellEditor cellEditor = new TableDynamicCellEditor(module, context);
        TableDynamicCellRenderer cellRenderer = new TableDynamicCellRenderer(module, context);

        // Column 1 (the map key)
        TableColumn keyColumn = table.getColumnModel().getColumn(0);
        keyColumn.setHeaderValue(COLUMN_NAMES[0]);
        keyColumn.setCellEditor(new DefaultCellEditor(new JBTextField()));

        // Column 2 (the map value)
        TableColumn valueColumn = table.getColumnModel().getColumn(1);
        valueColumn.setHeaderValue(COLUMN_NAMES[1]);
        valueColumn.setCellRenderer(cellRenderer);
        valueColumn.setCellEditor(cellEditor);
    }
}
