package com.reedelk.plugin.editor.properties.renderer.typemap.dynamic;

import com.intellij.openapi.module.Module;
import com.intellij.ui.table.JBTable;
import com.reedelk.module.descriptor.model.property.MapDescriptor;
import com.reedelk.plugin.editor.properties.commons.DisposableTableColumnModelFactory;
import com.reedelk.plugin.editor.properties.commons.StripedRowCellRenderer;
import com.reedelk.plugin.editor.properties.commons.TableDynamicCellEditor;
import com.reedelk.plugin.editor.properties.commons.TableDynamicCellRenderer;
import com.reedelk.plugin.editor.properties.context.ContainerContext;

import javax.swing.table.TableColumn;
import java.util.Optional;

import static com.reedelk.plugin.message.ReedelkBundle.message;

public class DynamicMapTableColumnModelFactory implements DisposableTableColumnModelFactory {

    private final String keyName;
    private final String valueName;
    private final TableDynamicCellEditor cellEditor;
    private final TableDynamicCellRenderer cellRenderer;

    DynamicMapTableColumnModelFactory(Module module,
                                      MapDescriptor propertyType,
                                      String componentPropertyPath,
                                      ContainerContext context) {
        keyName = Optional.ofNullable(propertyType.getKeyName()).orElse(message("table.header.key.name"));
        valueName = Optional.ofNullable(propertyType.getValueName()).orElse(message("table.header.value.name"));
        cellEditor = new TableDynamicCellEditor(module, componentPropertyPath, context);
        cellRenderer = new TableDynamicCellRenderer(module, componentPropertyPath, context);
    }

    @Override
    public void create(JBTable table) {
        // Column 1 (the map key)
        TableColumn keyColumn = table.getColumnModel().getColumn(0);
        keyColumn.setHeaderValue(keyName);
        keyColumn.setCellRenderer(new StripedRowCellRenderer());

        // Column 2 (the map value)
        TableColumn valueColumn = table.getColumnModel().getColumn(1);
        valueColumn.setHeaderValue(valueName);
        valueColumn.setCellEditor(cellEditor);
        valueColumn.setCellRenderer(cellRenderer);
    }
}
