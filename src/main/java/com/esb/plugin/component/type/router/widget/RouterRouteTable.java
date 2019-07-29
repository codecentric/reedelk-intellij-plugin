package com.esb.plugin.component.type.router.widget;

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

    public RouterRouteTable(ConditionRouteTableModel model) {
        final TableColumnModel tableColumnModel = new ConditionRouteTableColumnModel();
        JBTable table = new JBTable(model, tableColumnModel);
        table.addMouseListener(new TableMouseListener(table));
        JScrollPane tableScrollPane = new JBScrollPane(table);
        tableScrollPane.setPreferredSize(tableScrollPaneDimension);

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

        add(tableScrollPane, NORTH);
        add(Box.createVerticalGlue(), CENTER);
    }

    class TableMouseListener extends MouseAdapter {

        private final JBTable table;

        TableMouseListener(JBTable table) {
            this.table = table;
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (e.getClickCount() == 1) {
                int row = table.rowAtPoint(e.getPoint());
                int column = table.columnAtPoint(e.getPoint());

                // The 'otherwise' cannot be edited.
                if (column == 0 && row != table.getModel().getRowCount() - 1) {
                    System.out.println("yes");
                }
            }
        }
    }
}
