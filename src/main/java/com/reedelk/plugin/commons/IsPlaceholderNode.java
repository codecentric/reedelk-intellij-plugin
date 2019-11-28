package com.reedelk.plugin.commons;

import com.reedelk.plugin.component.type.placeholder.PlaceholderNode;
import com.reedelk.plugin.graph.node.GraphNode;

public class IsPlaceholderNode {

    private IsPlaceholderNode() {
    }

    public static boolean of(GraphNode node) {
        return node instanceof PlaceholderNode;
    }
}
