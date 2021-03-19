package de.codecentric.reedelk.plugin.component.type.stop;

import de.codecentric.reedelk.plugin.graph.FlowGraph;
import de.codecentric.reedelk.plugin.graph.deserializer.AbstractNodeDeserializer;
import de.codecentric.reedelk.plugin.graph.deserializer.DeserializerContext;
import de.codecentric.reedelk.plugin.graph.node.GraphNode;
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
