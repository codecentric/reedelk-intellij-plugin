package com.reedelk.plugin.graph.serializer;

import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.node.GraphNode;
import org.json.JSONArray;

public interface Serializer {

    default void serialize(FlowGraph graph, JSONArray sequence, GraphNode node, GraphNode stop) {
        throw new UnsupportedOperationException();
    }
}
