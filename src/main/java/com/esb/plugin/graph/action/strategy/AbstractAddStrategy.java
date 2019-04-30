package com.esb.plugin.graph.action.strategy;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.connector.Connector;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.ScopeBoundaries;
import com.esb.plugin.graph.node.ScopedNode;
import com.esb.plugin.graph.scope.FindScopes;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;

abstract class AbstractAddStrategy implements AddStrategy {

    protected final FlowGraph graph;
    protected final Point dropPoint;
    protected final Connector connector;
    protected final Graphics2D graphics;

    public AbstractAddStrategy(FlowGraph graph, Point dropPoint, Connector connector, Graphics2D graphics) {
        this.graph = graph;
        this.graphics = graphics;
        this.dropPoint = dropPoint;
        this.connector = connector;
    }

    protected void addToScopeIfNeeded(GraphNode closestPrecedingNode) {
        if (closestPrecedingNode instanceof ScopedNode) {
            ScopedNode scopedNode = (ScopedNode) closestPrecedingNode;
            connector.addToScope(scopedNode);
        } else {
            List<ScopedNode> scopedNodeObjects = FindScopes.of(graph, closestPrecedingNode);
            scopedNodeObjects.forEach(connector::addToScope);
        }
    }

    static int getScopeMaxXBound(@NotNull FlowGraph graph, @NotNull ScopedNode scopedNode, @NotNull Graphics2D graphics) {
        ScopeBoundaries scopeBoundaries = scopedNode.getScopeBoundaries(graph, graphics);
        return scopeBoundaries.getX() + scopeBoundaries.getWidth();
    }
}
