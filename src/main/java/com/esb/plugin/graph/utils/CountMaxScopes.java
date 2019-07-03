package com.esb.plugin.graph.utils;

import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.ScopedGraphNode;

import java.util.Collection;

public class CountMaxScopes {

    private CountMaxScopes() {
    }

    public static int of(ScopedGraphNode scope, GraphNode target) {
        return countMaxNestedScopes(scope, target, 0);
    }

    private static int countMaxNestedScopes(ScopedGraphNode scopedNode, GraphNode target, int max) {
        Collection<GraphNode> scope = scopedNode.getScope();
        if (scope.contains(target)) return max + 1;
        int currentMax = max;
        for (GraphNode node : scope) {
            if (node instanceof ScopedGraphNode) {
                int nestedMax = countMaxNestedScopes((ScopedGraphNode) node, target, max + 1);
                if (nestedMax > currentMax) {
                    currentMax = nestedMax;
                }
            }
        }
        return currentMax;
    }
}
