package com.esb.plugin.graph.scope;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.GraphNode;
import com.esb.plugin.graph.drawable.ScopedDrawable;

public class CountNestedScopes {

    public static int of(FlowGraph graph, GraphNode target) {
        if (target instanceof ScopedDrawable) {
            ScopedDrawable scopedDrawable = (ScopedDrawable) target;
            if (scopedDrawable.getScope().isEmpty()) {
                return 1 + FindScope.of(graph, scopedDrawable)
                        .map(scope -> 1 + of(graph, scope))
                        .orElse(0);
            }
        }
        return FindScope.of(graph, target)
                .map(scopedDrawable -> 1 + of(graph, scopedDrawable))
                .orElse(0);
    }
}
