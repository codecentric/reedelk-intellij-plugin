package com.reedelk.plugin.graph.action.replace;

import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.action.Action;
import com.reedelk.plugin.graph.node.GraphNode;

import java.util.ArrayList;
import java.util.List;

abstract class BaseActionNodeReplace implements Action {

    private final GraphNode from;
    private final GraphNode to;

    BaseActionNodeReplace(GraphNode from, GraphNode to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public void execute(FlowGraph graph) {
        List<GraphNode> predecessors = new ArrayList<>(graph.predecessors(from));
        List<GraphNode> successors = new ArrayList<>(graph.successors(from));

        // It is root (this could happen in a subflow)
        if (predecessors.isEmpty()) {
            graph.root(to);
        }

        // Remove predecessors edges incoming to 'from'
        // Add predecessors incoming to 'to'
        for (GraphNode predecessor : predecessors) {
            graph.remove(predecessor, from);
            graph.add(predecessor, to);
        }

        // The same as above but with outgoing
        for (GraphNode successor : successors) {
            graph.remove(from, successor);
            graph.add(to, successor);
        }

        // We must remove the from node from the graph
        graph.remove(from);
    }
}
