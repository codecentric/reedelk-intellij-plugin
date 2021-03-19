package de.codecentric.reedelk.plugin.commons;

import de.codecentric.reedelk.plugin.component.type.placeholder.PlaceholderNode;
import de.codecentric.reedelk.plugin.graph.node.GraphNode;

public class IsPlaceholderNode {

    private IsPlaceholderNode() {
    }

    public static boolean of(GraphNode node) {
        return node instanceof PlaceholderNode;
    }
}
