package com.esb.plugin.component.type.choice;

import com.esb.plugin.component.domain.ComponentData;
import com.esb.plugin.component.type.choice.widget.ChoiceRouteTable;
import com.esb.plugin.component.type.choice.widget.ConditionRouteTableModel;
import com.esb.plugin.component.type.generic.GenericComponentPropertiesRenderer;
import com.esb.plugin.graph.FlowSnapshot;
import com.esb.plugin.graph.node.GraphNode;
import com.intellij.ui.components.JBPanel;

import java.awt.*;
import java.util.List;

import static com.esb.plugin.component.type.choice.ChoiceNode.DATA_CONDITION_ROUTE_PAIRS;
import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.NORTH;

public class ChoicePropertiesRenderer extends GenericComponentPropertiesRenderer {

    public ChoicePropertiesRenderer(FlowSnapshot snapshot) {
        super(snapshot);
    }

    @Override
    public JBPanel render(GraphNode choiceNode) {
        JBPanel genericProperties = super.render(choiceNode);

        ComponentData componentData = choiceNode.componentData();
        List<ChoiceConditionRoutePair> conditionRoutePairList = componentData.get(DATA_CONDITION_ROUTE_PAIRS);

        ConditionRouteTableModel model = new ConditionRouteTableModel(conditionRoutePairList, snapshot);
        ChoiceRouteTable choiceRouteTable = new ChoiceRouteTable(model);

        JBPanel container = new JBPanel();
        container.setLayout(new BorderLayout());
        container.add(genericProperties, NORTH);
        container.add(choiceRouteTable, CENTER);
        return container;
    }
}
