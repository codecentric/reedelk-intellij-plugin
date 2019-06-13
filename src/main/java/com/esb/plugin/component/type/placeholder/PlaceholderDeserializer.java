package com.esb.plugin.component.type.placeholder;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.deserializer.AbstractNodeDeserializer;
import com.esb.plugin.graph.deserializer.DeserializerContext;
import com.esb.plugin.graph.node.GraphNode;
import org.json.JSONObject;

import static com.esb.internal.commons.JsonParser.Implementor;

public class PlaceholderDeserializer extends AbstractNodeDeserializer {

    public PlaceholderDeserializer(FlowGraph graph, DeserializerContext context) {
        super(graph, context);
    }

    @Override
    public GraphNode deserialize(GraphNode parent, JSONObject jsonDefinition) {

        String name = Implementor.name(jsonDefinition);

        GraphNode placeholderNode = context.instantiateGraphNode(name);

        graph.add(parent, placeholderNode);

        return placeholderNode;
    }

}
