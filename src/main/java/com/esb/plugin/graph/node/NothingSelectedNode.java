package com.esb.plugin.graph.node;

import com.esb.plugin.component.Component;
import com.esb.plugin.graph.FlowGraph;

import java.awt.*;
import java.awt.image.ImageObserver;

public class NothingSelectedNode implements GraphNode {

    @Override
    public void draw(FlowGraph graph, Graphics2D graphics, ImageObserver observer) {
        // nothing to draw
    }

    @Override
    public boolean isSelected() {
        return false;
    }

    @Override
    public Component component() {
        return null;
    }
}
