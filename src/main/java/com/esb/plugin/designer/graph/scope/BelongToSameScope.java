package com.esb.plugin.designer.graph.scope;

import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.drawable.Drawable;
import com.esb.plugin.designer.graph.drawable.ScopedDrawable;

import java.util.Optional;

public class BelongToSameScope {

    public static boolean from(FlowGraph graph, Drawable drawable1, Drawable drawable2) {
        Optional<ScopedDrawable> scope1 = FindScope.of(graph, drawable1);
        Optional<ScopedDrawable> scope2 = FindScope.of(graph, drawable2);
        if (!scope1.isPresent() && !scope2.isPresent()) {
            // they both don't belong to ANY scope.
            return true;
        }
        if (scope1.isPresent()) {
            if (scope2.isPresent()) {
                return scope1.get() == scope2.get();
            }
        }
        return false;
    }

}