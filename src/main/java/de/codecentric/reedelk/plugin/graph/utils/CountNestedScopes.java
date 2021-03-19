package de.codecentric.reedelk.plugin.graph.utils;

import de.codecentric.reedelk.plugin.graph.FlowGraph;
import de.codecentric.reedelk.plugin.graph.node.GraphNode;
import de.codecentric.reedelk.plugin.commons.IsScopedGraphNode;

public class CountNestedScopes {

    private CountNestedScopes() {
    }

    public static int of(FlowGraph graph, GraphNode target) {
        if (IsScopedGraphNode.of(target)) {
            return 1 + FindScope.of(graph, target)
                    .map(scopedNode -> of(graph, scopedNode))
                    .orElse(0);
        }
        return internalOf(graph, target);
    }

    private static int internalOf(FlowGraph graph, GraphNode target) {
        return FindScope.of(graph, target)
                .map(scopedNode -> 1 + internalOf(graph, scopedNode))
                .orElse(0);
    }
}
