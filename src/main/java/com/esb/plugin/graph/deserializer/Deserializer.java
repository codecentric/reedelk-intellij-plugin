package com.esb.plugin.graph.deserializer;


import com.esb.plugin.graph.node.GraphNode;
import org.json.JSONObject;

public interface Deserializer {

    GraphNode deserialize(GraphNode parent, JSONObject componentDefinition);

}
