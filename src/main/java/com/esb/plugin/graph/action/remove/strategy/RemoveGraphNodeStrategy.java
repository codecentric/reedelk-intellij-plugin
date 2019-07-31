package com.esb.plugin.graph.action.remove.strategy;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.action.Strategy;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.ScopedGraphNode;
import com.esb.plugin.graph.utils.FindScope;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class RemoveGraphNodeStrategy implements Strategy {

    private final FlowGraph graph;

    public RemoveGraphNodeStrategy(@NotNull FlowGraph graph) {
        this.graph = graph;
    }

    @Override
    public void execute(GraphNode toRemove) {
        List<GraphNode> predecessors = graph.predecessors(toRemove);
        List<GraphNode> successors = graph.successors(toRemove);

        GraphNode successor = successors.isEmpty() ? null : successors.get(0);

        if (predecessors.isEmpty()) {
            // It is the first node of the graph
            graph.remove(toRemove);
        } else {
            for (GraphNode predecessor : predecessors) {
                if (predecessor instanceof ScopedGraphNode) {
                    removeSuccessorOfScopedNode(toRemove, (ScopedGraphNode) predecessor, successor);
                } else {
                    removeSuccessorOfNodeStrategy(toRemove, predecessor, successor);
                }
            }
        }

        // Remove the node from any scope it might belong to
        Optional<ScopedGraphNode> selectedScope = FindScope.of(graph, toRemove);
        selectedScope.ifPresent(scopedNode -> scopedNode.removeFromScope(toRemove));
    }

    private void removeSuccessorOfNodeStrategy(GraphNode toRemove, GraphNode predecessor, GraphNode successor) {
        graph.remove(predecessor, toRemove);

        if (successor != null) graph.remove(toRemove, successor);

        graph.remove(toRemove);

        FindScope.of(graph, toRemove).ifPresent(scopedGraphNode ->
                scopedGraphNode.removeFromScope(scopedGraphNode));

        if (successor != null) graph.add(predecessor, successor);
    }

    private void removeSuccessorOfScopedNode(GraphNode toRemove, ScopedGraphNode predecessor, GraphNode successor) {
        int index = getToBeRemovedNodeIndex(predecessor, toRemove);

        graph.remove(predecessor, toRemove);
        FindScope.of(graph, toRemove)
                .ifPresent(scopedGraphNode -> scopedGraphNode.removeFromScope(toRemove));

        if (successor != null) {
            graph.remove(toRemove, successor);

            if (predecessor.scopeContains(successor)) {
                graph.add(predecessor, successor, index);

            } else if (predecessor.getScope().isEmpty()) {
                // We need to connect the scoped node with the next
                // one only if it is empty.
                graph.add(predecessor, successor);
            }
        }

        graph.remove(toRemove);
    }

    private int getToBeRemovedNodeIndex(ScopedGraphNode scopedPredecessor, GraphNode toRemove) {
        List<GraphNode> successors = graph.successors(scopedPredecessor);
        for (int i = 0; i < successors.size(); i++) {
            if (successors.get(i) == toRemove) return i;
        }
        // This is the case where we need to find a toRemove index
        // for a scoped predecessor without successors in the scope.
        // In this case, the index is just 0 since it is the first to be connected.
        return 0;
    }
}
