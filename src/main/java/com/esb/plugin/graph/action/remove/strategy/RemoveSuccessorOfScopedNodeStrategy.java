package com.esb.plugin.graph.action.remove.strategy;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.action.Strategy;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.ScopedGraphNode;
import com.esb.plugin.graph.utils.FindScope;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RemoveSuccessorOfScopedNodeStrategy implements Strategy {

    private final FlowGraph graph;
    private final GraphNode successor;
    private final GraphNode predecessor;

    public RemoveSuccessorOfScopedNodeStrategy(@NotNull FlowGraph graph, GraphNode predecessor, GraphNode successor) {
        this.graph = graph;
        this.predecessor = predecessor;
        this.successor = successor;
    }

    @Override
    public void execute(GraphNode toRemove) {

        ScopedGraphNode scopedPredecessor = (ScopedGraphNode) predecessor;

        int index = getDroppedIndex(scopedPredecessor, toRemove);

        graph.remove(predecessor, toRemove);

        FindScope.of(graph, toRemove)
                .ifPresent(scopedGraphNode -> scopedGraphNode.removeFromScope(toRemove));

        if (successor != null) {

            graph.remove(toRemove, successor);

            if (scopedPredecessor.scopeContains(successor)) {

                graph.add(predecessor, successor, index);

            } else if (scopedPredecessor.getScope().isEmpty()) {
                // We need to connect the scoped node with the next
                // one only if it is empty.
                graph.add(predecessor, successor);
            }
        }

        graph.remove(toRemove);
    }

    private int getDroppedIndex(ScopedGraphNode scopedPredecessor, GraphNode dropped) {
        List<GraphNode> successors = graph.successors(scopedPredecessor);
        for (int i = 0; i < successors.size(); i++) {
            if (successors.get(i) == dropped) return i;
        }
        // This is the case where we need to find a toRemove index
        // for a scoped predecessor without successors in the scope.
        // In this case, the index is just 0 since it is the first to be connected.
        return 0;
    }
}
