package com.esb.plugin.designer.graph.scope;

import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.drawable.Drawable;
import com.esb.plugin.designer.graph.drawable.ScopedDrawable;

import java.util.Optional;

public class IsLastScopeBeforeNode {

    public static boolean of(FlowGraph graph, ScopedDrawable scope, Drawable firstNodeOutsideScope) {
        Optional<ScopedDrawable> possibleScope = FindScope.of(graph, firstNodeOutsideScope);
        return possibleScope
                .map(s -> s.scopeContains(scope))
                .orElseGet(() -> !FindScope.of(graph, scope).isPresent());
    }
}
