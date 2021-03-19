package de.codecentric.reedelk.plugin.graph.deserializer;


import de.codecentric.reedelk.plugin.graph.node.GraphNode;
import org.json.JSONObject;

public interface Deserializer {

    GraphNode deserialize(GraphNode parent, JSONObject componentDefinition);
}
