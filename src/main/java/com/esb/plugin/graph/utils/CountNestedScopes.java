package com.esb.plugin.graph.utils;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.ScopedGraphNode;

public class CountNestedScopes {

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
