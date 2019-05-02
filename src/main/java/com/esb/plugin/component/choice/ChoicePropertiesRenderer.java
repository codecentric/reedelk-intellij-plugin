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

        TableColumn column = table.getColumnModel().getColumn(1);
        column.setCellEditor(new DefaultCellEditor(ROUTES));

        //Create the scroll pane and add the table to it.
        JScrollPane tableScrollPane = new JBScrollPane(table);
        tableScrollPane.setPreferredSize(new Dimension(0, 110));

        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BorderLayout());
        wrapper.add(tableScrollPane, BorderLayout.NORTH);
        wrapper.add(Box.createVerticalGlue(), BorderLayout.CENTER);


        JBPanel addConditionPanel = createAddConditionPanel();

        JBPanel panel = new JBPanel();
        panel.setBackground(Color.cyan);
        panel.setLayout(new BorderLayout());
        panel.add(addConditionPanel, BorderLayout.NORTH);
        panel.add(wrapper, BorderLayout.CENTER);
        return panel;
    }

    private JBPanel createAddConditionPanel() {
        JBPanel addConditionBox = new JBPanel();
        addConditionBox.setBackground(Color.GREEN);
        addConditionBox.setLayout(new GridBagLayout());

        FormUtility formUtility = new FormUtility();
        formUtility.addLabel("Condition:", addConditionBox);
        formUtility.addMiddleField(new JBTextField(), addConditionBox);

        JButton btnAddCondition = new JButton("Add");
        btnAddCondition.setSize(new Dimension(50, 20));

        JBPanel selectRouteBox = new JBPanel();
        selectRouteBox.setLayout(new GridBagLayout());

        formUtility.addLabel("Route:", selectRouteBox);
        formUtility.addMiddleField(ROUTES, selectRouteBox);
        formUtility.addLabel(btnAddCondition, selectRouteBox);
        formUtility.addLastField(selectRouteBox, addConditionBox);

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

    private static final JComboBox<String> ROUTES = new ComboBox<>();

    static {
        ROUTES.addItem("Route1");
        ROUTES.addItem("Route2");
        ROUTES.addItem("Route3");
        ROUTES.addItem("Route4");
    }
}
