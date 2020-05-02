package com.reedelk.plugin.editor.properties.renderer.typemap.dynamic;

import com.intellij.openapi.module.Module;
import com.intellij.ui.table.JBTable;
import com.reedelk.module.descriptor.model.property.MapDescriptor;
import com.reedelk.plugin.editor.properties.commons.*;

import javax.swing.table.TableColumn;
import java.util.Optional;

import static com.reedelk.plugin.message.ReedelkBundle.message;

public class DynamicMapTableColumnModelFactory implements DisposableTableColumnModelFactory {

    private final Module module;
    private final String keyName;
    private final String valueName;
    private final ContainerContext context;

    DynamicMapTableColumnModelFactory(Module module, MapDescriptor propertyType, ContainerContext context) {
        this.module = module;
        this.context = context;
        this.keyName = Optional.ofNullable(propertyType.getKeyName()).orElse(message("table.header.key.name"));
        this.valueName = Optional.ofNullable(propertyType.getValueName()).orElse(message("table.header.value.name"));
    }

    @Override
    public void create(JBTable table) {
        TableDynamicCellEditor cellEditor = new TableDynamicCellEditor(module, context);
        TableDynamicCellRenderer cellRenderer = new TableDynamicCellRenderer(module, context);

        // Column 1 (the map key)
        TableColumn keyColumn = table.getColumnModel().getColumn(0);
        keyColumn.setHeaderValue(keyName);
        keyColumn.setCellRenderer(new StripedRowCellRenderer());

        // Column 2 (the map value)
        TableColumn valueColumn = table.getColumnModel().getColumn(1);
        valueColumn.setHeaderValue(valueName);
        valueColumn.setCellRenderer(cellRenderer);
        valueColumn.setCellEditor(cellEditor);
    }
}
