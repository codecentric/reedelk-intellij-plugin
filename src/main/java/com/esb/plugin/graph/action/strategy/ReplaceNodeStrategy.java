package com.esb.plugin.graph.action.strategy;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.utils.FindScope;

import java.util.List;

import static com.esb.internal.commons.Preconditions.checkState;

/**
 * Strategy which replaces a node with the given replacement node.
 */
public class ReplaceNodeStrategy implements Strategy {

    private final FlowGraph graph;
    private final GraphNode toBeReplaced;

    ReplaceNodeStrategy(FlowGraph graph, GraphNode toBeReplaced) {
        this.graph = graph;
        this.toBeReplaced = toBeReplaced;
    }

    @Override
    public void execute(GraphNode replacement) {

        // Connect predecessors
        List<GraphNode> predecessorsOfPlaceHolder = graph.predecessors(toBeReplaced);
        if (predecessorsOfPlaceHolder.isEmpty()) {
            // If there are no predecessors, then the placeholder must be the root node.
            checkState(graph.root() == toBeReplaced,
                    "Expected Placeholder to be root node but it was not");
            graph.root(replacement);
        }

        predecessorsOfPlaceHolder.forEach(predecessor -> graph.add(predecessor, replacement));

        List<GraphNode> successorsOfPlaceHolder = graph.successors(toBeReplaced);
        successorsOfPlaceHolder.forEach(successor -> graph.add(replacement, successor));

        // If the placeholder belongs to a scope, we must
        // remove it from the scope and add the replacement to the scope.
        FindScope.of(graph, toBeReplaced).ifPresent(scopeNode -> {
            scopeNode.addToScope(replacement);
            scopeNode.removeFromScope(toBeReplaced);
        });

        // Remove the placeholder node from the graph (including inbound/outbound edges)
        graph.remove(toBeReplaced);
    }
}
