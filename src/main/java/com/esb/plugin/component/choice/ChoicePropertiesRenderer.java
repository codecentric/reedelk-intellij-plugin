package com.esb.plugin.component.choice;

import com.esb.plugin.component.choice.widget.AddConditionRoute;
import com.esb.plugin.component.choice.widget.ChoiceRouteTable;
import com.esb.plugin.component.choice.widget.RouteComboBox;
import com.esb.plugin.designer.properties.AbstractPropertyRenderer;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBPanel;

import javax.swing.*;
import java.awt.*;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.NORTH;

public class ChoicePropertiesRenderer extends AbstractPropertyRenderer {

    public ChoicePropertiesRenderer(Module module, FlowGraph graph, VirtualFile file) {
        super(module, graph, file);
    }

    @Override
    public JBPanel render(GraphNode choiceNode) {
        AddConditionRoute addConditionRoute = new AddConditionRoute(createRoutesCombo(choiceNode));
        ChoiceRouteTable choiceRouteTable = new ChoiceRouteTable(createRoutesCombo(choiceNode));
        addConditionRoute.addListener(choiceRouteTable);

        JBPanel panel = new JBPanel();
        panel.setLayout(new BorderLayout());
        panel.add(addConditionRoute, NORTH);
        panel.add(choiceRouteTable, CENTER);
        return panel;
    }

    private JComboBox<GraphNode> createRoutesCombo(GraphNode node) {
        JComboBox<GraphNode> routesCombo = new RouteComboBox();
        graph.successors(node).forEach(routesCombo::addItem);
        return routesCombo;
    }

}
