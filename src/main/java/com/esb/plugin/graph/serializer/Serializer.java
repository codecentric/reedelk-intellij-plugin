package com.esb.plugin.graph.serializer;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import org.json.JSONArray;

public interface Serializer {

    void serialize(FlowGraph graph, JSONArray sequence, GraphNode node, GraphNode stop);

}
