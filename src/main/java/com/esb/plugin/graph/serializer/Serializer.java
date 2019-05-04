package com.esb.plugin.graph.serializer;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import org.json.JSONObject;

public interface Serializer {

    JSONObject serialize(FlowGraph graph, GraphNode node);

}
