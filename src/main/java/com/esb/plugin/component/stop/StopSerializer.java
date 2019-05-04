package com.esb.plugin.component.stop;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.serializer.AbstractSerializer;
import org.json.JSONObject;

public class StopSerializer extends AbstractSerializer {

    @Override
    public JSONObject serialize(FlowGraph graph, GraphNode node) {
        throw new UnsupportedOperationException();
    }
}
