package com.esb.plugin.component.choice;

import com.esb.plugin.component.ComponentData;
import com.esb.plugin.component.ComponentPropertyDescriptor;
import com.esb.plugin.component.choice.widget.ChoiceRouteTable;
import com.esb.plugin.component.choice.widget.ConditionRouteTableModel;
import com.esb.plugin.designer.properties.renderer.node.AbstractNodePropertiesRenderer;
import com.esb.plugin.designer.properties.renderer.property.TypeRendererFactory;
import com.esb.plugin.designer.properties.widget.DefaultPropertiesPanel;
import com.esb.plugin.graph.GraphSnapshot;
import com.esb.plugin.graph.node.GraphNode;
import com.intellij.ui.components.JBPanel;

import java.awt.*;
import java.util.List;

import static com.esb.plugin.component.choice.ChoiceNode.DATA_CONDITION_ROUTE_PAIRS;
import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.NORTH;

public class ChoicePropertiesRenderer extends AbstractNodePropertiesRenderer {

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

        DefaultPropertiesPanel panel = new DefaultPropertiesPanel();

        List<ComponentPropertyDescriptor> componentProperties = componentData.getComponentPropertyDescriptors();
        componentProperties.forEach(propertyDescriptor ->
                TypeRendererFactory.get()
                        .from(propertyDescriptor.getPropertyType())
                        .render(propertyDescriptor, componentData, panel, snapshot));

        JBPanel container = new JBPanel();
        container.setLayout(new BorderLayout());
        container.add(panel, NORTH);
        container.add(choiceRouteTable, CENTER);
        return container;
    }

}
