package com.esb.plugin.graph.action.add;

import com.esb.plugin.graph.action.Action;
import com.esb.plugin.graph.node.GraphNode;

import java.awt.*;
import java.awt.image.ImageObserver;

public abstract class ActionNodeAdd implements Action {

    protected final ImageObserver observer;
    protected final Graphics2D graphics;
    protected final Point dropPoint;
    protected final GraphNode node;

    ActionNodeAdd(final Point dropPoint, final GraphNode node, final Graphics2D graphics, final ImageObserver observer) {
        this.dropPoint = dropPoint;
        this.observer = observer;
        this.graphics = graphics;
        this.node = node;
    }
}
