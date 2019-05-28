package com.esb.plugin.graph.utils;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.ScopedGraphNode;

public class CountNestedScopes {

    public static int of(FlowGraph graph, GraphNode target) {
        if (target instanceof ScopedGraphNode) {
            ScopedGraphNode scopedGraphNode = (ScopedGraphNode) target;
            if (scopedGraphNode.getScope().isEmpty()) {
                return 1 + FindScope.of(graph, scopedGraphNode)
                        .map(scope -> 1 + of(graph, scope))
                        .orElse(0);
            }
        }
        return FindScope.of(graph, target)
                .map(scopedNode -> 1 + of(graph, scopedNode))
                .orElse(0);
    }
}
