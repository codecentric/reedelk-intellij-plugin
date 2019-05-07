package com.esb.plugin.component.choice;

import com.esb.plugin.component.ComponentData;
import com.esb.plugin.component.choice.widget.ChoiceRouteTable;
import com.esb.plugin.component.choice.widget.ConditionRouteTableModel;
import com.esb.plugin.designer.properties.AbstractPropertyRenderer;
import com.esb.plugin.designer.properties.widget.PropertyBox;
import com.esb.plugin.graph.GraphSnapshot;
import com.esb.plugin.graph.node.GraphNode;
import com.intellij.ui.components.JBPanel;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.NORTH;

public class ChoicePropertiesRenderer extends AbstractPropertyRenderer {

    public ChoicePropertiesRenderer(GraphSnapshot snapshot) {
        super(snapshot);
    }

    @Override
    public JBPanel render(GraphNode choiceNode) {
        ComponentData componentData = choiceNode.component();
        Map<GraphNode, String> nodeConditionMap = (Map<GraphNode, String>) componentData.get("nodeConditionMap");

        List<ChoiceConditionRoutePair> pairs = new ArrayList<>();


        nodeConditionMap.forEach((node, condition) -> pairs.add(new ChoiceConditionRoutePair(condition, node)));

        ConditionRouteTableModel model = new ConditionRouteTableModel(pairs, choiceNode, snapshot);
        ChoiceRouteTable choiceRouteTable = new ChoiceRouteTable(model);

        // TODO: Fix this
        PropertyBox descriptionProperty = createPropertyBox(componentData, "Description");

        JBPanel choicePropertiesPanel = new JBPanel();
        choicePropertiesPanel.setLayout(new BorderLayout());
        choicePropertiesPanel.add(descriptionProperty, NORTH);
        choicePropertiesPanel.add(choiceRouteTable, CENTER);
        return choicePropertiesPanel;
    }

}
