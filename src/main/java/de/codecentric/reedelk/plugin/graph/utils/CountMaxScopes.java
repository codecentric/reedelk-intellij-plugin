package de.codecentric.reedelk.plugin.graph.utils;

import de.codecentric.reedelk.plugin.commons.IsScopedGraphNode;
import de.codecentric.reedelk.plugin.graph.node.GraphNode;
import de.codecentric.reedelk.plugin.graph.node.ScopedGraphNode;

import java.util.Collection;

public class CountMaxScopes {

    private CountMaxScopes() {
    }

    public static int of(ScopedGraphNode scope, GraphNode target) {
        if (IsScopedGraphNode.of(target)) {
            // If the target is a scope itself, it counts as a scope as well.
            return maxNestedScopes(scope, target, 1);
        } else {
            return maxNestedScopes(scope, target, 0);
        }
    }

    private static int maxNestedScopes(ScopedGraphNode scopedNode, GraphNode target, int max) {
        Collection<GraphNode> scope = scopedNode.getScope();
        if (scope.contains(target)) return max + 1;
        int currentMax = max;
        for (GraphNode node : scope) {
            if (IsScopedGraphNode.of(node)) {
                int nestedMax = maxNestedScopes((ScopedGraphNode) node, target, max + 1);
                if (nestedMax > currentMax) {
                    currentMax = nestedMax;
                }
            }
        }
        return currentMax;
    }
}
