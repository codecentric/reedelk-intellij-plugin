package com.esb.plugin.component.choice.widget;

import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;

import javax.swing.*;
import javax.swing.table.TableColumnModel;
import java.awt.*;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.NORTH;

public class ChoiceRouteTable extends JBPanel implements AddConditionRouteListener {

    private final Dimension tableScrollPaneDimension = new Dimension(0, 110);

    private final ConditionRouteTableModel tableModel;

    public ChoiceRouteTable(JComboBox routesComboBox) {
        tableModel = new ConditionRouteTableModel();
        final TableColumnModel tableColumnModel = new ConditionRouteTableColumnModel(routesComboBox);
        JBTable table = new JBTable(tableModel, tableColumnModel);

        JScrollPane tableScrollPane = new JBScrollPane(table);
        tableScrollPane.setPreferredSize(tableScrollPaneDimension);

        setLayout(new BorderLayout());
        add(tableScrollPane, NORTH);
        add(Box.createVerticalGlue(), CENTER);
    }


    @Override
    public void addRouteCondition(ConditionRoutePair conditionRoute) {
        tableModel.addConditionRoutePair(conditionRoute);
    }
}
