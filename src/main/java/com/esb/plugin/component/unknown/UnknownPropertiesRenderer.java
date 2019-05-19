package com.esb.plugin.component.unknown;

import com.esb.plugin.designer.properties.renderer.node.AbstractNodePropertiesRenderer;
import com.esb.plugin.graph.GraphSnapshot;
import com.esb.plugin.graph.node.GraphNode;
import com.intellij.ui.components.JBPanel;

import java.awt.*;

public class UnknownPropertiesRenderer extends AbstractNodePropertiesRenderer {

    public UnknownPropertiesRenderer(GraphSnapshot snapshot) {
        super(snapshot);
    }

    @Override
    public JBPanel render(GraphNode node) {
        JBPanel jbPanel = new JBPanel();
        jbPanel.setBackground(Color.GREEN);
        return jbPanel;
    }
}
