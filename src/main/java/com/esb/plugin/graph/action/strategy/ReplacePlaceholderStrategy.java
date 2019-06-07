package com.esb.plugin.graph.action.strategy;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.utils.FindScope;

import java.awt.*;
import java.util.List;

/**
 * Strategy which replaces a Placeholder node with the given replacement node.
 */
public class ReplacePlaceholderStrategy extends AbstractStrategy {

    public ReplacePlaceholderStrategy(FlowGraph graph, Point dropPoint, GraphNode replacement, Graphics2D graphics) {
        super(graph, dropPoint, replacement, graphics);
    }

    @Override
    public void execute(GraphNode placeHolder) {

        // Connect predecessors
        List<GraphNode> predecessorsOfPlaceHolder = graph.predecessors(placeHolder);
        predecessorsOfPlaceHolder.forEach(predecessor -> graph.add(predecessor, node));

        // Connect successors
        List<GraphNode> successorsOfPlaceHolder = graph.successors(placeHolder);
        successorsOfPlaceHolder.forEach(successor -> graph.add(node, successor));

        // If the placeholder belongs to a scope, we must
        // remove it from the scope and add the replacement to the scope.
        FindScope.of(graph, placeHolder).ifPresent(scopeNode -> {
            scopeNode.addToScope(node);
            scopeNode.removeFromScope(placeHolder);
        });

        // Remove the placeholder node from the graph (including inbound/outbound edges)
        graph.remove(placeHolder);
    }
}
