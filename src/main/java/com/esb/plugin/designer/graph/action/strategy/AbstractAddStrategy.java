package com.esb.plugin.designer.graph.action.strategy;

import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.ScopeUtilities;
import com.esb.plugin.designer.graph.connector.Connector;
import com.esb.plugin.designer.graph.drawable.Drawable;

import java.awt.*;

abstract class AbstractAddStrategy implements AddStrategy {

    protected final FlowGraph graph;
    protected final Point dropPoint;
    protected final Connector connector;

    public AbstractAddStrategy(FlowGraph graph, Point dropPoint, Connector connector) {
        this.graph = graph;
        this.dropPoint = dropPoint;
        this.connector = connector;
    }

    protected void addToScopeIfNeeded(Drawable closestPrecedingNode) {
        ScopeUtilities.addToScopeIfNecessary(graph, closestPrecedingNode, connector);
    }
}
