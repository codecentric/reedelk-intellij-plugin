package com.esb.plugin.graph.action.strategy;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.utils.FindScope;

import java.util.List;

/**
 * Strategy which replaces a Placeholder node with the given replacement node.
 */
public class ReplacePlaceholderStrategy implements Strategy {

    private final FlowGraph graph;
    private final GraphNode placeHolder;


    public ReplacePlaceholderStrategy(FlowGraph graph, GraphNode placeHolder) {
        this.graph = graph;
        this.placeHolder = placeHolder;
    }

    @Override
    public void execute(GraphNode replacement) {

        // Connect predecessors
        List<GraphNode> predecessorsOfPlaceHolder = graph.predecessors(placeHolder);
        predecessorsOfPlaceHolder.forEach(predecessor -> graph.add(predecessor, replacement));

        // Connect successors
        List<GraphNode> successorsOfPlaceHolder = graph.successors(placeHolder);
        successorsOfPlaceHolder.forEach(successor -> graph.add(replacement, successor));

        // If the placeholder belongs to a scope, we must
        // remove it from the scope and add the replacement to the scope.
        FindScope.of(graph, placeHolder).ifPresent(scopeNode -> {
            scopeNode.addToScope(replacement);
            scopeNode.removeFromScope(placeHolder);
        });

        // Remove the placeholder node from the graph (including inbound/outbound edges)
        graph.remove(placeHolder);
    }
}
