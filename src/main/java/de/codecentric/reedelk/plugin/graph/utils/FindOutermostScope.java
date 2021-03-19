package de.codecentric.reedelk.plugin.graph.utils;

import de.codecentric.reedelk.plugin.graph.FlowGraph;
import de.codecentric.reedelk.plugin.graph.node.GraphNode;
import de.codecentric.reedelk.plugin.graph.node.ScopedGraphNode;

import java.util.List;
import java.util.Optional;
import java.util.Stack;

public class FindOutermostScope {

    private FindOutermostScope() {
    }

    public static Optional<ScopedGraphNode> of(FlowGraph graph, List<GraphNode> predecessors) {
        if (predecessors.isEmpty()) return Optional.empty();

        // It is enough to take in consideration only one predecessor, because
        // all the predecessors must belong to the same outerscope if any of the children
        // belongs to it.
        GraphNode target = predecessors.get(0);
        Stack<ScopedGraphNode> stack = FindScopes.of(graph, target);
        ScopedGraphNode current = null;
        while (!stack.isEmpty()) {
            current = stack.pop();
        }
        return Optional.ofNullable(current);
    }

}
