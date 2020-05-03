package com.reedelk.plugin.editor.properties.renderer.typemap.dynamic;

import com.intellij.openapi.module.Module;
import com.intellij.ui.table.JBTable;
import com.reedelk.module.descriptor.model.property.MapDescriptor;
import com.reedelk.plugin.editor.properties.commons.DisposableTableColumnModelFactory;
import com.reedelk.plugin.editor.properties.commons.StripedRowCellRenderer;
import com.reedelk.plugin.editor.properties.commons.TableDynamicCellEditor;
import com.reedelk.plugin.editor.properties.commons.TableDynamicCellRenderer;

import javax.swing.table.TableColumn;
import java.util.Optional;

import static com.reedelk.plugin.message.ReedelkBundle.message;

public class DynamicMapTableColumnModelFactory implements DisposableTableColumnModelFactory {

    private final Module module;
    private final String keyName;
    private final String valueName;
    private final String componentPropertyPath;

    DynamicMapTableColumnModelFactory(Module module, MapDescriptor propertyType, String componentPropertyPath) {
        this.module = module;
        this.componentPropertyPath = componentPropertyPath;
        this.keyName = Optional.ofNullable(propertyType.getKeyName()).orElse(message("table.header.key.name"));
        this.valueName = Optional.ofNullable(propertyType.getValueName()).orElse(message("table.header.value.name"));
    }

    @Override
    public void create(JBTable table) {
        TableDynamicCellEditor cellEditor = new TableDynamicCellEditor(module, componentPropertyPath);
        TableDynamicCellRenderer cellRenderer = new TableDynamicCellRenderer(module, componentPropertyPath);

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
