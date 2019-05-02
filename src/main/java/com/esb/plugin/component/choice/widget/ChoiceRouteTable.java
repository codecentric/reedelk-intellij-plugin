package com.esb.plugin.component.choice.widget;

import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;

import javax.swing.*;
import javax.swing.table.TableColumnModel;
import java.awt.*;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.NORTH;

public class ChoiceRouteTable extends JBPanel {

    private final Dimension tableScrollPaneDimension = new Dimension(0, 110);

    public ChoiceRouteTable(JComboBox routesComboBox) {
        final ConditionRouteTableModel tableModel = new ConditionRouteTableModel();
        final TableColumnModel tableColumnModel = new ConditionRouteTableColumnModel(routesComboBox);
        final JBTable table = new JBTable(tableModel, tableColumnModel);


        JScrollPane tableScrollPane = new JBScrollPane(table);
        tableScrollPane.setPreferredSize(tableScrollPaneDimension);

        setLayout(new BorderLayout());
        add(tableScrollPane, NORTH);
        add(Box.createVerticalGlue(), CENTER);

        tableModel.addConditionRoutePair("default", "FlowReference");
    }


}
