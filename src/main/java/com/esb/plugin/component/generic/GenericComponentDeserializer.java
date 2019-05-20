package com.esb.plugin.component.generic;

import com.esb.internal.commons.JsonParser;
import com.esb.plugin.component.ComponentData;
import com.esb.plugin.component.TypeDescriptor;
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

        // This one might be GenericComponent node OR Unknown Node.
        GraphNode node = context.instantiateGraphNode(name);

        ComponentData componentData = node.componentData();

        jsonDefinition.keys().forEachRemaining(propertyName ->
                componentData.getPropertyDescriptor(propertyName).ifPresent(descriptor -> {

                    // Here this call should return Unknown Type for Unknown Component property.
                    TypeDescriptor propertyType = descriptor.getPropertyType();
                    Object propertyValue =
                            ValueConverterFactory.forType(propertyType).from(propertyName, jsonDefinition);
                    componentData.set(propertyName, propertyValue);
                }));

        graph.add(parent, node);
        return node;
    }
}
