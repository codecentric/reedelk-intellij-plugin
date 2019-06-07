package com.esb.plugin.graph.action.strategy;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.ScopeBoundaries;
import com.esb.plugin.graph.node.ScopedGraphNode;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

abstract class AbstractStrategy implements Strategy {

    protected final Graphics2D graphics;
    protected final FlowGraph graph;
    protected final Point dropPoint;
    protected final GraphNode node;

    AbstractStrategy(final FlowGraph graph, final Point dropPoint, final GraphNode node, final Graphics2D graphics) {
        this.dropPoint = dropPoint;
        this.graphics = graphics;
        this.graph = graph;
        this.node = node;
    }

    static int getScopeMaxXBound(@NotNull FlowGraph graph, @NotNull ScopedGraphNode scopedGraphNode, @NotNull Graphics2D graphics) {
        ScopeBoundaries scopeBoundaries = scopedGraphNode.getScopeBoundaries(graph, graphics);
        return scopeBoundaries.getX() + scopeBoundaries.getWidth();
    }

}
