package com.esb.plugin.component.choice;

import com.esb.plugin.component.ComponentData;
import com.esb.plugin.component.choice.widget.ChoiceRouteTable;
import com.esb.plugin.component.choice.widget.ConditionRouteTableModel;
import com.esb.plugin.designer.properties.AbstractPropertiesRenderer;
import com.esb.plugin.designer.properties.widget.DefaultPropertiesPanel;
import com.esb.plugin.graph.GraphSnapshot;
import com.esb.plugin.graph.node.GraphNode;
import com.intellij.ui.components.JBPanel;

import java.awt.*;
import java.util.List;

import static com.esb.plugin.component.choice.ChoiceNode.DATA_CONDITION_ROUTE_PAIRS;
import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.NORTH;
import static org.apache.commons.lang3.StringUtils.capitalize;

public class ChoicePropertiesRenderer extends AbstractPropertiesRenderer {

    public ChoicePropertiesRenderer(GraphSnapshot snapshot) {
        super(snapshot);
    }

    @Override
    public JBPanel render(GraphNode choiceNode) {
        ComponentData componentData = choiceNode.componentData();
        List<ChoiceConditionRoutePair> conditionRoutePairList =
                (List<ChoiceConditionRoutePair>) componentData.get(DATA_CONDITION_ROUTE_PAIRS);

        ConditionRouteTableModel model = new ConditionRouteTableModel(conditionRoutePairList, snapshot);
        ChoiceRouteTable choiceRouteTable = new ChoiceRouteTable(model);

        DefaultPropertiesPanel propertiesListPanel = new DefaultPropertiesPanel(snapshot, componentData);

        componentData.descriptorProperties().forEach(propertyName ->
                propertiesListPanel.addPropertyField(capitalize(propertyName), propertyName.toLowerCase()));

        JBPanel container = new JBPanel();
        container.setLayout(new BorderLayout());
        container.add(propertiesListPanel, NORTH);
        container.add(choiceRouteTable, CENTER);

        return container;
    }

}
