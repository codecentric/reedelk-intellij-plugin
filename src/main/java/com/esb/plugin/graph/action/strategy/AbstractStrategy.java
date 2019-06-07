package com.esb.plugin.graph.action.strategy;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.ScopeBoundaries;
import com.esb.plugin.graph.node.ScopedGraphNode;
import com.esb.plugin.graph.utils.FindScope;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

abstract class AbstractStrategy implements Strategy {

    protected final FlowGraph graph;
    protected final Point dropPoint;
    protected final GraphNode node;
    protected final Graphics2D graphics;

    AbstractStrategy(FlowGraph graph, Point dropPoint, GraphNode node, Graphics2D graphics) {
        this.graph = graph;
        this.graphics = graphics;
        this.dropPoint = dropPoint;
        this.node = node;
    }

    void addToScopeIfNeeded(@NotNull GraphNode closestPrecedingNode) {
        FindScope.of(graph, closestPrecedingNode)
                .ifPresent(scopedGraphNode ->
                        scopedGraphNode.addToScope(node));
    }

    static int getScopeMaxXBound(@NotNull FlowGraph graph, @NotNull ScopedGraphNode scopedGraphNode, @NotNull Graphics2D graphics) {
        ScopeBoundaries scopeBoundaries = scopedGraphNode.getScopeBoundaries(graph, graphics);
        return scopeBoundaries.getX() + scopeBoundaries.getWidth();
    }
}
