package com.reedelk.plugin.component.type.router.widget;

import com.intellij.openapi.module.Module;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.table.JBTable;
import com.reedelk.plugin.editor.properties.widget.PropertyTable;
import com.reedelk.plugin.editor.properties.widget.input.script.EditScriptDialog;

import javax.swing.*;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.NORTH;

public class RouterRouteTable extends JBPanel {

    private final Module module;
    private final JBTable table;

    public RouterRouteTable(Module module, ConditionRouteTableModel model) {
        this.module = module;

        TableColumnModel tableColumnModel = new ConditionRouteTableColumnModel();

        PropertyTable propertyTable = new PropertyTable(model, tableColumnModel);
        this.table = propertyTable.getTable();
        this.table.addMouseListener(new OpenEditScriptDialogMouseListener());

        setLayout(new BorderLayout());
        add(propertyTable, NORTH);
        add(Box.createVerticalGlue(), CENTER);
    }

    /**
     * Listener which displays Script Editor Dialog when any of the 'edit script'
     * icon in the first column is clicked.
     */
    class OpenEditScriptDialogMouseListener extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent event) {
            int row = table.rowAtPoint(event.getPoint());
            int column = table.columnAtPoint(event.getPoint());
            // The 'otherwise' script (column 0) cannot be edited. If we click on
            // the Edit script we show a popup containing the Script Editor.
            if (column == 0 && row != table.getModel().getRowCount() - 1) {
                String scriptCellValue = (String) table.getModel().getValueAt(row, column + 1);
                EditScriptDialog editScriptDialog = new EditScriptDialog(module, scriptCellValue);
                if (editScriptDialog.showAndGet()) {
                    String updatedValue = editScriptDialog.getValue();
                    table.getModel().setValueAt(updatedValue, row, column + 1);
                }
            }
        }
    }
}
