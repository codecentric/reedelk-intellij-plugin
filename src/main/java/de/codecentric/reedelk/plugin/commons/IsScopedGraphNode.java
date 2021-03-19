package de.codecentric.reedelk.plugin.commons;

import de.codecentric.reedelk.plugin.graph.node.GraphNode;
import de.codecentric.reedelk.plugin.graph.node.ScopedGraphNode;

public class IsScopedGraphNode {

    private IsScopedGraphNode() {
    }

    public static boolean of(GraphNode node) {
        return node instanceof ScopedGraphNode;
    }
}
