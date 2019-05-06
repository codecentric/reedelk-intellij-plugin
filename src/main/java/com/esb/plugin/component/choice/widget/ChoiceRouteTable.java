package com.esb.plugin.component.choice.widget;

import com.esb.plugin.component.choice.ChoiceConditionRoutePair;
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
    private final ConditionRouteTableModel model;

    public ChoiceRouteTable(ConditionRouteTableModel model) {
        this.model = model;

        final TableColumnModel tableColumnModel = new ConditionRouteTableColumnModel();
        JBTable table = new JBTable(model, tableColumnModel);
        JScrollPane tableScrollPane = new JBScrollPane(table);
        tableScrollPane.setPreferredSize(tableScrollPaneDimension);

        setLayout(new BorderLayout());
        add(tableScrollPane, NORTH);
        add(Box.createVerticalGlue(), CENTER);
    }

    @Override
    public void addRouteCondition(ChoiceConditionRoutePair conditionRoute) {
        model.addConditionRoutePair(conditionRoute);
    }
}
