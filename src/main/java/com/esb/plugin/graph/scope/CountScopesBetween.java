package com.esb.plugin.graph.scope;

import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.ScopedNode;

import java.util.Optional;

public class CountScopesBetween {

    public static Optional<Integer> them(ScopedNode scopedNode, GraphNode target) {
        return scopesBetween(0, scopedNode, target);
    }

    private static Optional<Integer> scopesBetween(int depth, ScopedNode scopedNode, GraphNode target) {
        if (scopedNode == target) return Optional.of(depth);
        if (scopedNode.getScope().isEmpty()) {
            return Optional.of(depth);
        }
        for (GraphNode drawableInScope : scopedNode.getScope()) {
            if (drawableInScope instanceof ScopedNode) {
                Optional<Integer> found = scopesBetween(depth + 1, (ScopedNode) drawableInScope, target);
                if (found.isPresent()) return found;
            } else if (drawableInScope == target) {
                return Optional.of(depth);
            }
        }
        return Optional.empty();
    }

}
