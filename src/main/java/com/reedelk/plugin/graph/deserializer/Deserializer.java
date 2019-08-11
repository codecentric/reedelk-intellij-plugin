package com.reedelk.plugin.graph.deserializer;


import com.reedelk.plugin.graph.node.GraphNode;
import org.json.JSONObject;

public interface Deserializer {

    default GraphNode deserialize(GraphNode parent, JSONObject componentDefinition) {
        throw new UnsupportedOperationException();
    }

}
