package com.reedelk.plugin.graph.utils;

import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.graph.node.ScopedGraphNode;

public class IsLastScopeBeforeNode {

    private IsLastScopeBeforeNode() {
    }

    public static boolean of(FlowGraph graph, ScopedGraphNode targetScope, GraphNode firstNodeOutsideScope) {
        return FindScope.of(graph, firstNodeOutsideScope)
                .map(currentScope -> currentScope.scopeContains(targetScope))
                .orElseGet(() -> !FindScope.of(graph, targetScope).isPresent());
    }
}
