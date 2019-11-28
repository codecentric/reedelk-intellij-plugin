package com.reedelk.plugin.commons;

import com.reedelk.plugin.graph.node.GraphNode;

public class IsNotScopedGraphNode {

    private IsNotScopedGraphNode() {
    }

    public static boolean of(GraphNode graphNode) {
        return !IsScopedGraphNode.of(graphNode);
    }
}
