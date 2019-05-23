package com.esb.plugin.component.type.generic;

import com.esb.internal.commons.JsonParser;
import com.esb.plugin.component.domain.ComponentData;
import com.esb.plugin.component.domain.TypeDescriptor;
import com.esb.plugin.converter.ValueConverterFactory;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.deserializer.AbstractDeserializer;
import com.esb.plugin.graph.deserializer.DeserializerContext;
import com.esb.plugin.graph.node.GraphNode;
import org.json.JSONObject;

public class GenericComponentDeserializer extends AbstractDeserializer {

    public GenericComponentDeserializer(FlowGraph graph, DeserializerContext context) {
        super(graph, context);
    }

    @Override
    public GraphNode deserialize(GraphNode parent, JSONObject jsonDefinition) {
        String name = JsonParser.Implementor.name(jsonDefinition);

        // The following graph node instance, might be generic
        // component or unknown node.
        GraphNode node = context.instantiateGraphNode(name);

        ComponentData componentData = node.componentData();

        jsonDefinition.keys().forEachRemaining(propertyName ->

                componentData.getPropertyDescriptor(propertyName).ifPresent(descriptor -> {

                    TypeDescriptor propertyType = descriptor.getPropertyType();

                    Object propertyValue = ValueConverterFactory
                            .forType(propertyType)
                            .from(propertyName, jsonDefinition);

                    componentData.set(propertyName, propertyValue);

                }));

        graph.add(parent, node);

        return node;
    }
}