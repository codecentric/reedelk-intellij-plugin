package com.esb.plugin.component.choice.widget;

import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import java.awt.*;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.NORTH;

public class ChoiceRouteTable extends JBPanel {

    private final Dimension tableScrollPaneDimension = new Dimension(0, 110);

    public ChoiceRouteTable() {
        final TableModel model = new MyTableModel();
        final JBTable table = new JBTable(model);

        final TableColumn column = table.getColumnModel().getColumn(1);
        column.setCellEditor(new DefaultCellEditor(ROUTES));


        JScrollPane tableScrollPane = new JBScrollPane(table);
        tableScrollPane.setPreferredSize(tableScrollPaneDimension);

        setLayout(new BorderLayout());
        add(tableScrollPane, NORTH);
        add(Box.createVerticalGlue(), CENTER);
    }

    class MyTableModel extends AbstractTableModel {
        String[] columnNames = {"Condition", "Route"};
        Object[][] data = {
                {"payload.name == 'Mark'", "route1"},
                {"payload.name == 'Anton'", "route2"},
        };

        @Override
        public int getRowCount() {
            return data.length;
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public String getColumnName(int col) {
            return columnNames[col];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return data[rowIndex][columnIndex];
        }

        @Override
        public Class getColumnClass(int columnIndex) {
            return getValueAt(0, columnIndex).getClass();
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            return true;
        }

        @Override
        public void setValueAt(Object value, int row, int col) {
            data[row][col] = value;
            fireTableCellUpdated(row, col);
        }

    }

    private static final JComboBox<String> ROUTES = new ComboBox<>();

    static {
        ROUTES.addItem("Route1");
        ROUTES.addItem("Route2");
        ROUTES.addItem("Route3");
        ROUTES.addItem("Route4");
    }
}
