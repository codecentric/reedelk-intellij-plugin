package com.reedelk.plugin.component.type.stop;

import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.deserializer.AbstractNodeDeserializer;
import com.reedelk.plugin.graph.deserializer.DeserializerContext;
import com.reedelk.plugin.graph.node.GraphNode;
import org.json.JSONObject;

public class StopDeserializer extends AbstractNodeDeserializer {

    public StopDeserializer(FlowGraph graph, GraphNode current, DeserializerContext context) {
        super(graph, current, context);
    }

    @Override
    public GraphNode deserialize(GraphNode parent, JSONObject componentDefinition) {
        throw new UnsupportedOperationException("Stop node deserializer");
    }
}
