package com.esb.plugin.component.choice;

import com.esb.plugin.component.choice.widget.AddRouteCondition;
import com.esb.plugin.component.choice.widget.ChoiceRouteTable;
import com.esb.plugin.designer.properties.AbstractPropertyRenderer;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBPanel;

import java.awt.*;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.NORTH;

public class ChoicePropertiesRenderer extends AbstractPropertyRenderer {

    public ChoicePropertiesRenderer(Module module, FlowGraph graph, VirtualFile file) {
        super(module, graph, file);
    }

    @Override
    public JBPanel render(GraphNode node) {
        JBPanel panel = new JBPanel();
        panel.setLayout(new BorderLayout());
        panel.add(new AddRouteCondition(), NORTH);
        panel.add(new ChoiceRouteTable(), CENTER);
        return panel;
    }


}
