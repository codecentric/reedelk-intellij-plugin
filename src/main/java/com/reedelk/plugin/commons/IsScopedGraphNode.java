package com.reedelk.plugin.commons;

import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.graph.node.ScopedGraphNode;

public class IsScopedGraphNode {

    public static boolean of(GraphNode node) {
        return node instanceof ScopedGraphNode;
    }
}
