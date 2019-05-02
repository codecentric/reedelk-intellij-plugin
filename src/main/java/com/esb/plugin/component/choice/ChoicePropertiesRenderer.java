package com.esb.plugin.component.choice;

import com.esb.plugin.component.choice.widget.AddConditionRoute;
import com.esb.plugin.component.choice.widget.ChoiceRouteTable;
import com.esb.plugin.designer.properties.AbstractPropertyRenderer;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBPanel;

import javax.swing.*;
import java.awt.*;

import static com.esb.plugin.designer.canvas.drawables.ComponentDescription.DESCRIPTION_PROPERTY_NAME;
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

    private JComboBox<String> createRoutesCombo(GraphNode node) {
        JComboBox<String> routesCombo = new ComboBox<>();
        graph.successors(node)
                .stream()
                .map(successor -> (String) successor.component().getData(DESCRIPTION_PROPERTY_NAME))
                .forEach(routesCombo::addItem);
        return routesCombo;
    }

}
