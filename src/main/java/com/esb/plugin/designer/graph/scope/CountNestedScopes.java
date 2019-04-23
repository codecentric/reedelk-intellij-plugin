package com.esb.plugin.designer.graph.scope;

import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.drawable.Drawable;
import com.esb.plugin.designer.graph.drawable.ScopedDrawable;

public class CountNestedScopes {

    public static int of(FlowGraph graph, Drawable target) {
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
