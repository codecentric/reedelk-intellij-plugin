package com.esb.plugin.component.choice;

import com.esb.plugin.designer.properties.AbstractPropertyRenderer;
import com.esb.plugin.designer.properties.widget.FormUtility;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.table.JBTable;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import java.awt.*;

public class ChoicePropertiesRenderer extends AbstractPropertyRenderer {

    public ChoicePropertiesRenderer(Module module, FlowGraph graph, VirtualFile file) {
        super(module, graph, file);
    }

    @Override
    public JBPanel render(GraphNode node) {
        TableModel model = new MyTableModel();

        final JBTable table = new JBTable(model);
        //table.setSize(new Dimension(200, 90));
        JComboBox<String> routeCombo = new ComboBox<>();
        routeCombo.addItem("Route1");
        routeCombo.addItem("Route2");
        routeCombo.addItem("Route3");
        routeCombo.addItem("Route4");

        TableColumn column = table.getColumnModel().getColumn(1);
        column.setCellEditor(new DefaultCellEditor(routeCombo));

        //Create the scroll pane and add the table to it.
        JScrollPane tableScrollPane = new JBScrollPane(table);
        tableScrollPane.setPreferredSize(new Dimension(100, 120));

        //Add the scroll pane to this panel.
        JBPanel panel = new JBPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

        JBPanel addConditionPanel = createAddConditionPanel();
        panel.add(addConditionPanel);
        panel.add(tableScrollPane);
        panel.add(Box.createVerticalGlue());
        return panel;
    }

    private JBPanel createAddConditionPanel() {
        JBPanel addConditionBox = new JBPanel();
        addConditionBox.setLayout(new GridBagLayout());

        FormUtility formUtility = new FormUtility();
        formUtility.addLabel("Condition:", addConditionBox);
        formUtility.addMiddleField(new JBTextField(), addConditionBox);

        JComboBox<String> routeCombo = new ComboBox<>();
        routeCombo.setSize(new Dimension(100, 20));
        routeCombo.addItem("Route1");
        routeCombo.addItem("Route2");
        routeCombo.addItem("Route3");
        routeCombo.addItem("Route4");

        JButton btnAddCondition = new JButton("Add");
        btnAddCondition.setSize(new Dimension(50, 20));

        JBPanel selectRouteBox = new JBPanel();
        selectRouteBox.setLayout(new GridBagLayout());

        formUtility.addLabel("Route:", selectRouteBox);
        formUtility.addMiddleField(routeCombo, selectRouteBox);
        formUtility.addLabel(btnAddCondition, selectRouteBox);

        formUtility.addLastField(selectRouteBox, addConditionBox);
        addConditionBox.add(Box.createVerticalGlue());
        return addConditionBox;
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
