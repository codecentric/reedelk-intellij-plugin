package com.esb.plugin.component.type.router.widget;

import com.esb.plugin.editor.properties.widget.input.script.EditScriptDialog;
import com.intellij.openapi.module.Module;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;

import javax.swing.*;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.NORTH;

public class RouterRouteTable extends JBPanel {

    private final Dimension tableScrollPaneDimension = new Dimension(0, 110);
    private final Module module;
    private final JBTable table;

    public RouterRouteTable(Module module, ConditionRouteTableModel model) {
        this.module = module;

        final TableColumnModel tableColumnModel = new ConditionRouteTableColumnModel();
        JBTable table = new JBTable(model, tableColumnModel);
        table.addMouseListener(new TableMouseListener());
        this.table = table;
        JScrollPane tableScrollPane = new JBScrollPane(table);
        tableScrollPane.setPreferredSize(tableScrollPaneDimension);

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

        add(tableScrollPane, NORTH);
        add(Box.createVerticalGlue(), CENTER);
    }

    class TableMouseListener extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent e) {
            if (e.getClickCount() == 1) {
                int row = table.rowAtPoint(e.getPoint());
                int column = table.columnAtPoint(e.getPoint());

                // The 'otherwise' script cannot be edited. If we click on
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
}
