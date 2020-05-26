package com.reedelk.plugin.graph.utils;

import com.reedelk.plugin.exception.PluginException;
import com.reedelk.plugin.graph.node.GraphNode;

import java.util.List;

public class FindContainingLayer {

    private FindContainingLayer() {
    }

    public static int of(List<List<GraphNode>> layers, GraphNode current) {
        for (int i = 0; i < layers.size(); i++) {
            if (layers.get(i).contains(current)) {
                return i;
            }
        }
        throw new PluginException("Could not find containing layer for node " + current);
    }
}
