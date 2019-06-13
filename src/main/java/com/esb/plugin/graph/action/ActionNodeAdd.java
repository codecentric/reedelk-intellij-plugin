package com.esb.plugin.graph.action;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;

import java.awt.*;
import java.awt.image.ImageObserver;

public abstract class ActionNodeAdd {

    protected final ImageObserver observer;
    protected final Graphics2D graphics;
    protected final Point dropPoint;
    protected final GraphNode node;

    public ActionNodeAdd(final Point dropPoint, final GraphNode node, final Graphics2D graphics, final ImageObserver observer) {
        this.dropPoint = dropPoint;
        this.observer = observer;
        this.graphics = graphics;
        this.node = node;
    }

    public abstract void execute(FlowGraph graph);
}
