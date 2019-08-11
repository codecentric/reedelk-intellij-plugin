package com.reedelk.plugin.graph.utils;

import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.graph.node.ScopedGraphNode;

public class CountNestedScopes {

    private CountNestedScopes() {
    }

    public static int of(FlowGraph graph, GraphNode target) {
        if (target instanceof ScopedGraphNode) {
            return 1 + FindScope.of(graph, target)
                    .map(scopedNode -> of(graph, scopedNode))
                    .orElse(0);
        }
        return _of(graph, target);
    }

    private static int _of(FlowGraph graph, GraphNode target) {
        return FindScope.of(graph, target)
                .map(scopedNode -> 1 + _of(graph, scopedNode))
                .orElse(0);
    }
}
