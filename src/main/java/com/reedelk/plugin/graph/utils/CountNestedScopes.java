package com.reedelk.plugin.graph.utils;

import com.reedelk.plugin.commons.IsScopedGraphNode;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.node.GraphNode;

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
