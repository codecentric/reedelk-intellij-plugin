package com.esb.plugin.component.flowreference;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.deserializer.AbstractDeserializer;
import com.esb.plugin.graph.deserializer.DeserializerContext;
import com.esb.plugin.graph.node.GraphNode;
import org.json.JSONObject;

import static com.esb.internal.commons.JsonParser.Implementor;

public class FlowReferenceDeserializer extends AbstractDeserializer {

    public FlowReferenceDeserializer(FlowGraph graph, DeserializerContext context) {
        super(graph, context);
    }

    @Override
    public GraphNode deserialize(GraphNode parent, JSONObject componentDefinition) {

        String name = Implementor.name(componentDefinition);

        FlowReferenceNode drawable = context.instantiateGraphNode(name);

        graph.add(parent, drawable);

        return drawable;
    }
}
