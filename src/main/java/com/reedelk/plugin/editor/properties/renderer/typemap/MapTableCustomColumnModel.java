package com.reedelk.plugin.editor.properties.renderer.typemap;

import com.intellij.ui.components.JBTextField;
import com.intellij.ui.table.JBTable;
import com.reedelk.plugin.editor.properties.commons.DisposableTableColumnModelFactory;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

public class MapTableCustomColumnModel implements DisposableTableColumnModelFactory {

    private static final String[] COLUMN_NAMES = {"Status Code", "Edit Response"};
    private static final int EDIT_COLUMN_WIDTH = 100;
    private final ActionHandler action;

    interface ActionHandler {
        void onClick(Object editorValue);
    }

    public MapTableCustomColumnModel(ActionHandler action) {
        this.action = action;
    }

    @Override
    public void create(JBTable table) {
        // Column 1 (the map key)
        TableColumn keyColumn = table.getColumnModel().getColumn(0);
        keyColumn.setHeaderValue(COLUMN_NAMES[0]);
        keyColumn.setCellEditor(new DefaultCellEditor(new JBTextField()));

        // Column 2 (Edit button)
        EditButtonColumn editButtonColumn = new EditButtonColumn(table, action);

        TableColumn valueColumn = table.getColumnModel().getColumn(1);
        valueColumn.setHeaderValue(COLUMN_NAMES[1]);
        valueColumn.setCellRenderer(editButtonColumn);
        valueColumn.setCellEditor(editButtonColumn);
        valueColumn.setPreferredWidth(EDIT_COLUMN_WIDTH);
        valueColumn.setMaxWidth(EDIT_COLUMN_WIDTH);

        table.addMouseListener(editButtonColumn);
        table.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {

            }

            @Override
            public void mouseMoved(MouseEvent e) {
                if (table.columnAtPoint(e.getPoint()) == 1) {
                    table.setCursor(new Cursor(Cursor.HAND_CURSOR));
                } else {
                    table.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }
            }
        });
    }
}
