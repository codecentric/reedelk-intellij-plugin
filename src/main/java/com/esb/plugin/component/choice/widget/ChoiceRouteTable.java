package com.esb.plugin.component.choice.widget;

import com.esb.plugin.component.ComponentData;
import com.esb.plugin.component.choice.ChoiceConditionRoutePair;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.manager.GraphChangeNotifier;
import com.esb.plugin.graph.node.GraphNode;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;

import javax.swing.*;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.util.List;
import java.util.Optional;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.NORTH;
import static java.util.stream.Collectors.toList;

public class ChoiceRouteTable extends JBPanel implements AddConditionRouteListener {

    private final Dimension tableScrollPaneDimension = new Dimension(0, 110);

    private final ConditionRouteTableModel tableModel;
    private final FlowGraph graph;
    private final Module module;
    private final VirtualFile file;
    private final GraphNode node;

    public ChoiceRouteTable(JComboBox routesComboBox, Module module, FlowGraph graph, VirtualFile file, GraphNode node) {
        this.module = module;
        this.graph = graph;
        this.file = file;
        this.node = node;
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
    public void addRouteCondition(ChoiceConditionRoutePair conditionRoute) {
        tableModel.addConditionRoutePair(conditionRoute);

        ComponentData component = node.component();

        List<ChoiceConditionRoutePair> data = tableModel.getData();
        Optional<ChoiceConditionRoutePair> otherwise = data
                .stream()
                .filter(choiceConditionRoutePair ->
                        choiceConditionRoutePair.getCondition().equals("otherwise"))
                .findAny();
        otherwise.ifPresent(choiceConditionRoutePair ->
                component.set("otherwise", choiceConditionRoutePair.getNext()));

        List<ChoiceConditionRoutePair> whenConditions = data
                .stream()
                .filter(choiceConditionRoutePair ->
                        !choiceConditionRoutePair.getCondition().equals("otherwise"))
                .collect(toList());
        component.set("when", whenConditions);

        GraphChangeNotifier notifier = module.getMessageBus().syncPublisher(GraphChangeNotifier.TOPIC);
        notifier.onChange(graph, file);
    }
}
