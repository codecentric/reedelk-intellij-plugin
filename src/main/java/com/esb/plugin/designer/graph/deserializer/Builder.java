package com.esb.plugin.designer.graph.deserializer;


import com.esb.plugin.designer.graph.GraphNode;
import org.json.JSONObject;

public interface Builder {

    GraphNode build(GraphNode parent, JSONObject componentDefinition);

}
