package com.esb.plugin.graph.action;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.ScopedGraphNode;

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
        checkState(predecessors.size() <= 1, "Expected at most one predecessor");
        checkState(successors.size() <= 1, "Expected at most one successor");

        if (predecessors.isEmpty()) {
            graph.remove(dropped, successors.get(0));
            graph.root(successors.get(0));

        } else {
            GraphNode predecessor = predecessors.get(0);
            GraphNode successor = successors.get(0);

            if (predecessor instanceof ScopedGraphNode) {
                int index = getDroppedIndex((ScopedGraphNode) predecessor, dropped);
                graph.remove(predecessor, dropped);
                graph.remove(dropped, successor);
                graph.add(predecessor, successor, index);

            } else {
                graph.remove(predecessor, dropped);
                graph.remove(dropped, successor);
                graph.add(predecessor, successor);
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
