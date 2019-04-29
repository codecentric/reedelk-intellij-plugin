package com.esb.plugin.designer.graph.action;

import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.GraphNode;
import com.esb.plugin.designer.graph.drawable.ScopedDrawable;

import java.util.List;

public class RemoveDrawableFromGraph {

    private final GraphNode dropped;
    private final FlowGraph graph;

    public RemoveDrawableFromGraph(final FlowGraph copy, final GraphNode dropped) {
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
                    if (predecessor instanceof ScopedDrawable) {
                        // We need to check if successor is in the scope.
                        ScopedDrawable scope = (ScopedDrawable) predecessor;
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
