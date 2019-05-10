package com.esb.plugin.graph.action;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.ScopedGraphNode;

import java.util.List;

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
        if (predecessors.isEmpty()) {
            graph.root(successors.get(0));
        } else {
            for (GraphNode predecessor : predecessors) {
                for (GraphNode successor : successors) {
                    if (predecessor instanceof ScopedGraphNode) {
                        // We need to check if successor is in the scope.
                        ScopedGraphNode scope = (ScopedGraphNode) predecessor;
                        if (scope.scopeContains(successor)) {
                            // then we can connect it because it is part of the same scope
                            graph.add(predecessor, successor);
                        }
                    } else {
                        graph.add(predecessor, successor);
                    }
                }
            }
        }
        graph.remove(dropped);
    }
}