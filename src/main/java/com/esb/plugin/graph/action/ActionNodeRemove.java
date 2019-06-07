package com.esb.plugin.graph.action;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.ScopedGraphNode;
import com.esb.plugin.graph.utils.FindScope;

import java.util.List;

import static com.esb.internal.commons.Preconditions.checkState;

public class ActionNodeRemove {

    private final GraphNode dropped;
    private final FlowGraph graph;

    public ActionNodeRemove(final FlowGraph copy, final GraphNode dropped) {
        this.graph = copy;
        this.dropped = dropped;
    }

    public void remove() {
        List<GraphNode> predecessors = graph.predecessors(dropped);
        List<GraphNode> successors = graph.successors(dropped);

        // We might remove the node right outside the scope, which
        // might have more than one predecessor
        //checkState(predecessors.size() <= 1, "Expected at most one predecessor");
        checkState(successors.size() <= 1, "Expected at most one successor");

        if (predecessors.isEmpty()) {
            graph.remove(dropped, successors.get(0));
            graph.root(successors.get(0));
            graph.remove(dropped);

        } else {

            GraphNode successor = successors.isEmpty() ? null : successors.get(0);

            for (GraphNode predecessor : predecessors) {
                if (predecessor instanceof ScopedGraphNode) {
                    ScopedGraphNode scopedPredecessor = (ScopedGraphNode) predecessor;
                    int index = getDroppedIndex(scopedPredecessor, dropped);
                    graph.remove(predecessor, dropped);

                    FindScope.of(graph, dropped)
                            .ifPresent(scopedGraphNode -> scopedGraphNode.removeFromScope(dropped));

                    if (successor != null) {
                        graph.remove(dropped, successor);
                        if (scopedPredecessor.scopeContains(successor)) {
                            graph.add(predecessor, successor, index);

                            // We need to connect the scoped node with the next one only
                            // if it is empty.
                        } else if (scopedPredecessor.getScope().isEmpty()) {
                            graph.add(predecessor, successor);
                        }
                    }

                    graph.remove(dropped);

                } else {
                    graph.remove(predecessor, dropped);
                    if (successor != null) graph.remove(dropped, successor);
                    graph.remove(dropped);
                    FindScope.of(graph, dropped)
                            .ifPresent(scopedGraphNode -> scopedGraphNode.removeFromScope(scopedGraphNode));
                    if (successor != null) graph.add(predecessor, successor);
                }
            }
        }
    }

    private int getDroppedIndex(ScopedGraphNode scopedPredecessor, GraphNode dropped) {
        List<GraphNode> successors = graph.successors(scopedPredecessor);
        for (int i = 0; i < successors.size(); i++) {
            if (successors.get(i) == dropped) return i;
        }
        throw new IllegalStateException("Could not find index");
    }
}
