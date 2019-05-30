package com.esb.plugin.editor.designer.widget;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;

import java.awt.*;
import java.awt.image.ImageObserver;

public class Arrows implements Widget {

    private int x;
    private int y;
    private final GraphNode parent;

    public Arrows(GraphNode parent) {
        this.parent = parent;
    }

    @Override
    public void draw(FlowGraph graph, Graphics2D graphics, ImageObserver observer) {
        graph.successors(parent).forEach(successor -> {

            Point sourceBaryCenter = parent.getBarycenter(graphics, observer);
            Point source = new Point(
                    sourceBaryCenter.x + Math.floorDiv(parent.width(graphics), 2) - 15,
                    sourceBaryCenter.y);

            Point targetBaryCenter = successor.getBarycenter(graphics, observer);
            Point target = new Point(
                    targetBaryCenter.x - Math.floorDiv(successor.width(graphics), 2) + 15,
                    targetBaryCenter.y);

            Arrow arrow = new Arrow(source, target);
            arrow.draw(graphics);
        });
    }

    @Override
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
}


