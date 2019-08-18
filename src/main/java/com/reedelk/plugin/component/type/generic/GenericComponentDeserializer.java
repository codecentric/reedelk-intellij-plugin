package com.reedelk.plugin.component.type.generic;

import com.reedelk.plugin.component.domain.ComponentData;
import com.reedelk.plugin.component.serialization.ComponentDataHolderDeserializer;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.deserializer.AbstractNodeDeserializer;
import com.reedelk.plugin.graph.deserializer.DeserializerContext;
import com.reedelk.plugin.graph.node.GraphNode;
import org.json.JSONObject;

import static com.reedelk.runtime.commons.JsonParser.Implementor;

public class GenericComponentDeserializer extends AbstractNodeDeserializer {

    public GenericComponentDeserializer(FlowGraph graph, DeserializerContext context) {
        super(graph, context);
    }

    @Override
    public GraphNode deserialize(GraphNode parent, JSONObject jsonDefinition) {
        String name = Implementor.name(jsonDefinition);

        // The following graph node instance, might be generic component or unknown node.
        GraphNode node = context.instantiateGraphNode(name);

        ComponentData componentData = node.componentData();

        jsonDefinition.keys().forEachRemaining(propertyName ->
                componentData.getPropertyDescriptor(propertyName)
                        .ifPresent(descriptor ->
                                ComponentDataHolderDeserializer.deserialize(jsonDefinition, componentData, descriptor)));

        graph.add(parent, node);

        return node;
    }
}
