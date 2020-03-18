package com.reedelk.plugin.editor.properties.renderer.typemap.primitive;

import com.intellij.ui.table.JBTable;
import com.reedelk.module.descriptor.model.TypeMapDescriptor;
import com.reedelk.plugin.editor.properties.commons.DisposableTableColumnModelFactory;
import com.reedelk.plugin.editor.properties.commons.StripedRowCellRenderer;

import javax.swing.table.TableColumn;
import java.util.Optional;

import static com.reedelk.plugin.message.ReedelkBundle.message;

public class MapTableColumnModelFactory implements DisposableTableColumnModelFactory {

    private final String keyName;
    private final String valueName;

    public MapTableColumnModelFactory(TypeMapDescriptor propertyType) {
        this.keyName = Optional.ofNullable(propertyType.getKeyName()).orElse(message("table.header.key.name"));
        this.valueName = Optional.ofNullable(propertyType.getValueName()).orElse(message("table.header.value.name"));
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
        valueColumn.setCellRenderer(new StripedRowCellRenderer());
    }
}
