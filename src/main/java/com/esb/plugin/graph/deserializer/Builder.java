package com.esb.plugin.graph.deserializer;


import com.esb.plugin.graph.node.GraphNode;
import org.json.JSONObject;

public interface Builder {

    GraphNode build(GraphNode parent, JSONObject componentDefinition);

}
