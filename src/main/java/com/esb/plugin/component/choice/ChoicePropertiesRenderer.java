package com.esb.plugin.component.choice;

import com.esb.plugin.designer.properties.AbstractPropertyRenderer;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

public class ChoicePropertiesRenderer extends AbstractPropertyRenderer {

    public ChoicePropertiesRenderer(Module module, FlowGraph graph, VirtualFile file) {
        super(module, graph, file);
    }

    @Override
    public JBPanel render(GraphNode node) {
        TableModel model = new MyTableModel();

        final JBTable table = new JBTable(model);

        JComboBox<String> routeCombo = new ComboBox<>();
        routeCombo.addItem("Route1");
        routeCombo.addItem("Route2");
        routeCombo.addItem("Route3");
        routeCombo.addItem("Route4");

        TableColumn column = table.getColumnModel().getColumn(1);
        column.setCellEditor(new DefaultCellEditor(routeCombo));

        //Create the scroll pane and add the table to it.
        JScrollPane scrollPane = new JBScrollPane(table);

        //Add the scroll pane to this panel.
        JBPanel panel = new JBPanel();
        panel.add(scrollPane);
        return panel;
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
}
