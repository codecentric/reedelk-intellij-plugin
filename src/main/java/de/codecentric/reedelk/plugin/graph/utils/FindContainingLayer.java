package de.codecentric.reedelk.plugin.graph.utils;

import de.codecentric.reedelk.plugin.exception.PluginException;
import de.codecentric.reedelk.plugin.graph.node.GraphNode;

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
