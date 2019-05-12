package com.esb.plugin.component.stop;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.serializer.Serializer;
import org.json.JSONArray;

public class StopSerializer implements Serializer {
    @Override
    public void serialize(FlowGraph graph, JSONArray sequence, GraphNode node, GraphNode stop) {
        throw new UnsupportedOperationException();
    }
}
