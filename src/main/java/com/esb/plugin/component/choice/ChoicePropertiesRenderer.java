package com.esb.plugin.component.choice;

import com.esb.plugin.component.ComponentData;
import com.esb.plugin.component.choice.widget.AddConditionRoute;
import com.esb.plugin.component.choice.widget.ChoiceRouteTable;
import com.esb.plugin.component.choice.widget.ConditionRouteTableModel;
import com.esb.plugin.component.choice.widget.RouteComboBox;
import com.esb.plugin.designer.properties.AbstractPropertyRenderer;
import com.esb.plugin.graph.GraphSnapshot;
import com.esb.plugin.graph.node.GraphNode;
import com.intellij.ui.components.JBPanel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.NORTH;

public class ChoicePropertiesRenderer extends AbstractPropertyRenderer {

    public ChoicePropertiesRenderer(GraphSnapshot snapshot) {
        super(snapshot);
    }

    @Override
    public JBPanel render(GraphNode choiceNode) {

        ComponentData componentData = choiceNode.component();
        List<ChoiceConditionRoutePair> when = (List<ChoiceConditionRoutePair>) componentData.get("when");
        List<ChoiceConditionRoutePair> conditionRoutePairList = new ArrayList<>(when);

        GraphNode otherwise = (GraphNode) componentData.get("otherwise");
        conditionRoutePairList.add(new ChoiceConditionRoutePair("otherwise", otherwise));


        ConditionRouteTableModel model = new ConditionRouteTableModel(conditionRoutePairList, choiceNode, snapshot);
        AddConditionRoute addConditionRoute = new AddConditionRoute(createRoutesCombo(choiceNode));

        ChoiceRouteTable choiceRouteTable = new ChoiceRouteTable(createRoutesCombo(choiceNode), model);
        addConditionRoute.addListener(choiceRouteTable);

        JBPanel choicePropertiesPanel = new JBPanel();
        choicePropertiesPanel.setLayout(new BorderLayout());
        choicePropertiesPanel.add(addConditionRoute, NORTH);
        choicePropertiesPanel.add(choiceRouteTable, CENTER);

        return choicePropertiesPanel;
    }

    private JComboBox<GraphNode> createRoutesCombo(GraphNode node) {
        JComboBox<GraphNode> routesCombo = new RouteComboBox();
        snapshot.getGraph().successors(node).forEach(routesCombo::addItem);
        return routesCombo;
    }

}
