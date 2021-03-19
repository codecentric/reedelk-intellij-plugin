package de.codecentric.reedelk.plugin.commons;

import de.codecentric.reedelk.plugin.graph.node.GraphNode;

public class IsNotScopedGraphNode {

    private IsNotScopedGraphNode() {
    }

    public static boolean of(GraphNode graphNode) {
        return !IsScopedGraphNode.of(graphNode);
    }
}
