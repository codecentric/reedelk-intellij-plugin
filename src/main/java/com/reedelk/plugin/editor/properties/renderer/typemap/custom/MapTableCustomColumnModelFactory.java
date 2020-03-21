package com.reedelk.plugin.editor.properties.renderer.typemap.custom;

import com.intellij.ui.table.JBTable;
import com.reedelk.module.descriptor.model.TypeMapDescriptor;
import com.reedelk.plugin.editor.properties.commons.DisposableTableColumnModelFactory;
import com.reedelk.plugin.editor.properties.commons.StripedRowCellRenderer;

import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.Optional;

import static com.reedelk.plugin.message.ReedelkBundle.message;

public class MapTableCustomColumnModelFactory implements DisposableTableColumnModelFactory {

    private static final Cursor CURSOR_HAND = new Cursor(Cursor.HAND_CURSOR);
    private static final Cursor CURSOR_DEFAULT = new Cursor(Cursor.DEFAULT_CURSOR);

    private static final int EDIT_COLUMN_WIDTH = 150;
    private final MapTableCustomEditButtonAction action;
    private final String keyName;
    private final String valueName;

    public MapTableCustomColumnModelFactory(TypeMapDescriptor propertyType, MapTableCustomEditButtonAction action) {
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
        MapTableCustomEditButtonCell editButtonColumn = new MapTableCustomEditButtonCell(table, action);

        TableColumn valueColumn = table.getColumnModel().getColumn(1);
        valueColumn.setHeaderValue(valueName);
        valueColumn.setCellRenderer(editButtonColumn);
        valueColumn.setCellEditor(editButtonColumn);
        valueColumn.setPreferredWidth(EDIT_COLUMN_WIDTH);
        valueColumn.setMaxWidth(EDIT_COLUMN_WIDTH);

        table.addMouseListener(editButtonColumn);
        table.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                // We are not interested in detecting mouse dragged event.
            }

            @Override
            public void mouseMoved(MouseEvent event) {
                if (table.columnAtPoint(event.getPoint()) == 1) {
                    table.setCursor(CURSOR_HAND);
                } else {
                    table.setCursor(CURSOR_DEFAULT);
                }
            }
        });
    }
}
