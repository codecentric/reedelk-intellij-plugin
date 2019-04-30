package com.esb.plugin.graph.scope;

import com.esb.plugin.graph.node.Drawable;
import com.esb.plugin.graph.node.ScopedDrawable;

import java.util.Optional;

public class CountScopesBetween {

    public static Optional<Integer> them(ScopedDrawable scopedDrawable, Drawable target) {
        return scopesBetween(0, scopedDrawable, target);
    }

    private static Optional<Integer> scopesBetween(int depth, ScopedDrawable scopedDrawable, Drawable target) {
        if (scopedDrawable == target) return Optional.of(depth);
        if (scopedDrawable.getScope().isEmpty()) {
            return Optional.of(depth);
        }
        for (Drawable drawableInScope : scopedDrawable.getScope()) {
            if (drawableInScope instanceof ScopedDrawable) {
                Optional<Integer> found = scopesBetween(depth + 1, (ScopedDrawable) drawableInScope, target);
                if (found.isPresent()) return found;
            } else if (drawableInScope == target) {
                return Optional.of(depth);
            }
        }
        return Optional.empty();
    }


}
