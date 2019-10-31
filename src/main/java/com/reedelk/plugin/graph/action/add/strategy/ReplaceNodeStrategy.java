package com.reedelk.plugin.graph.action.add.strategy;

import com.reedelk.plugin.commons.GetSuccessorIndex;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.action.Strategy;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.graph.node.ScopedGraphNode;
import com.reedelk.plugin.graph.utils.FindScope;

import java.util.List;

import static com.reedelk.runtime.commons.Preconditions.checkState;

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

        if (isPredecessorScopedNode(predecessorsOfPlaceHolder)) {
            // If the predecessors is only one and it is a scoped node we must
            // replace the 'replacement' at the index of the 'toBeReplaced' node.
            ScopedGraphNode predecessorScopedGraphNode = (ScopedGraphNode) predecessorsOfPlaceHolder.get(0);
            int index = GetSuccessorIndex.ofScopedNode(graph, predecessorScopedGraphNode, toBeReplaced);
            graph.add(predecessorScopedGraphNode, replacement, index);
        } else {
            predecessorsOfPlaceHolder.forEach(predecessor -> graph.add(predecessor, replacement));
        }


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

    private static boolean isPredecessorScopedNode(List<GraphNode> predecessors) {
        return predecessors.size() == 1 && predecessors.get(0) instanceof ScopedGraphNode;
    }
}
