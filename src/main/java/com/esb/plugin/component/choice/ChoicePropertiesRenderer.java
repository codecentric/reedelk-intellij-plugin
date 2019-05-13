package com.esb.plugin.component.choice;

import com.esb.plugin.component.ComponentData;
import com.esb.plugin.component.choice.widget.ChoiceRouteTable;
import com.esb.plugin.component.choice.widget.ConditionRouteTableModel;
import com.esb.plugin.designer.properties.AbstractPropertyRenderer;
import com.esb.plugin.designer.properties.widget.FormBuilder;
import com.esb.plugin.graph.GraphSnapshot;
import com.esb.plugin.graph.node.GraphNode;
import com.intellij.ui.components.JBPanel;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.NORTH;

public class ChoicePropertiesRenderer extends AbstractPropertyRenderer {

    public ChoicePropertiesRenderer(GraphSnapshot snapshot) {
        super(snapshot);
    }

    @Override
    public JBPanel render(GraphNode choiceNode) {
        ComponentData componentData = choiceNode.componentData();
        List<ChoiceConditionRoutePair> conditionRoutePairList = (List<ChoiceConditionRoutePair>) componentData.get(ChoiceNode.DATA_CONDITION_ROUTE_PAIRS);

        ConditionRouteTableModel model = new ConditionRouteTableModel(conditionRoutePairList, snapshot);
        ChoiceRouteTable choiceRouteTable = new ChoiceRouteTable(model);

        // TODO: Fix this
        JBPanel descriptionProperty = new JBPanel();
        descriptionProperty.setLayout(new GridBagLayout());
        descriptionProperty.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        FormBuilder.get()
                .addLabel("Description", descriptionProperty)
                .addLastField(new JTextField(), descriptionProperty);


        JBPanel container = new JBPanel();
        container.setLayout(new BorderLayout());
        container.add(descriptionProperty, NORTH);
        container.add(choiceRouteTable, CENTER);

        return container;
    }

}
