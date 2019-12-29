package com.reedelk.plugin.component.type.placeholder;

import com.reedelk.component.descriptor.ComponentData;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.deserializer.AbstractNodeDeserializer;
import com.reedelk.plugin.graph.deserializer.DeserializerContext;
import com.reedelk.plugin.graph.node.GraphNode;
import org.json.JSONObject;

import static com.reedelk.runtime.commons.JsonParser.Implementor;

public class PlaceholderDeserializer extends AbstractNodeDeserializer {

    public PlaceholderDeserializer(FlowGraph graph, DeserializerContext context) {
        super(graph, context);
    }

    @Override
    public GraphNode deserialize(GraphNode parent, JSONObject jsonDefinition) {

        String name = Implementor.name(jsonDefinition);

        GraphNode placeholderNode = context.instantiateGraphNode(name);

        ComponentData componentData = placeholderNode.componentData();

        if (jsonDefinition.has(Implementor.description())) {
            componentData.set(Implementor.description(), Implementor.description(jsonDefinition));
        }

        graph.add(parent, placeholderNode);

        return placeholderNode;
    }
}
