package com.esb.plugin.graph.utils;

import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.ScopedGraphNode;

import java.util.Optional;

public class CountScopesBetween {

    public static Optional<Integer> them(ScopedGraphNode scopedGraphNode, GraphNode target) {
        return scopesBetween(0, scopedGraphNode, target);
    }

    private static Optional<Integer> scopesBetween(int depth, ScopedGraphNode scopedGraphNode, GraphNode target) {
        if (scopedGraphNode == target) return Optional.of(depth);
        if (scopedGraphNode.getScope().isEmpty()) {
            return Optional.of(depth);
        }
        for (GraphNode drawableInScope : scopedGraphNode.getScope()) {
            if (drawableInScope instanceof ScopedGraphNode) {
                Optional<Integer> found = scopesBetween(depth + 1, (ScopedGraphNode) drawableInScope, target);
                if (found.isPresent()) return found;
            } else if (drawableInScope == target) {
                return Optional.of(depth);
            }
        }
        return Optional.empty();
    }

}
