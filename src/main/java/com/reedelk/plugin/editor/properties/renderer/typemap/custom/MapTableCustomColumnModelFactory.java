package com.reedelk.plugin.editor.properties.renderer.typemap.custom;

import com.intellij.ui.table.JBTable;
import com.reedelk.module.descriptor.model.property.MapDescriptor;
import com.reedelk.plugin.editor.properties.commons.DisposableTableColumnModelFactory;
import com.reedelk.plugin.editor.properties.commons.StripedRowCellRenderer;
import com.reedelk.plugin.editor.properties.commons.TableEditButtonCellEditor;

import javax.swing.table.TableColumn;
import java.util.Optional;

import static com.reedelk.plugin.editor.properties.commons.TableEditButtonCellEditor.TableCustomEditButtonAction;
import static com.reedelk.plugin.message.ReedelkBundle.message;

public class MapTableCustomColumnModelFactory implements DisposableTableColumnModelFactory {

    private static final int EDIT_COLUMN_WIDTH = 150;
    private final TableCustomEditButtonAction action;
    private final String keyName;
    private final String valueName;

    public MapTableCustomColumnModelFactory(MapDescriptor propertyType, TableCustomEditButtonAction action) {
        this.action = action;
        this.keyName = Optional.ofNullable(propertyType.getKeyName()).orElse(message("table.header.key.name"));
        this.valueName = Optional.ofNullable(propertyType.getValueName()).orElse(message("table.header.value.name"));
    }

    @Override
    public void create(JBTable table) {
        // Column 1 (the map key)
        TableColumn keyColumn = table.getColumnModel().getColumn(0);
        keyColumn.setHeaderValue(keyName);
        keyColumn.setCellRenderer(new StripedRowCellRenderer());

        // Column 2 (Edit button)
        TableEditButtonCellEditor editButtonColumn = new TableEditButtonCellEditor(table, action);

        TableColumn valueColumn = table.getColumnModel().getColumn(1);
        valueColumn.setHeaderValue(valueName);
        valueColumn.setCellRenderer(editButtonColumn);
        valueColumn.setCellEditor(editButtonColumn);
        valueColumn.setPreferredWidth(EDIT_COLUMN_WIDTH);
        valueColumn.setMaxWidth(EDIT_COLUMN_WIDTH);
    }
}
