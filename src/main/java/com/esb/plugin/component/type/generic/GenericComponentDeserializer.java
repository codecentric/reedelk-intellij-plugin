package com.esb.plugin.component.type.generic;

import com.esb.internal.commons.JsonParser;
import com.esb.plugin.component.domain.*;
import com.esb.plugin.converter.ValueConverterFactory;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.deserializer.AbstractNodeDeserializer;
import com.esb.plugin.graph.deserializer.DeserializerContext;
import com.esb.plugin.graph.node.GraphNode;
import org.json.JSONObject;

import static com.esb.internal.commons.JsonParser.Implementor;

public class GenericComponentDeserializer extends AbstractNodeDeserializer {

    public GenericComponentDeserializer(FlowGraph graph, DeserializerContext context) {
        super(graph, context);
    }

    @Override
    public GraphNode deserialize(GraphNode parent, JSONObject jsonDefinition) {
        String name = Implementor.name(jsonDefinition);

        // The following graph node instance, might be generic
        // component or unknown node.
        GraphNode node = context.instantiateGraphNode(name);

        ComponentData componentData = node.componentData();

        jsonDefinition.keys().forEachRemaining(propertyName ->
                componentData.getPropertyDescriptor(propertyName)
                        .ifPresent(descriptor -> deserialize(jsonDefinition, componentData, descriptor)));

        graph.add(parent, node);

        return node;
    }

    // TODO: Fixme
    private void deserialize(JSONObject parent, ComponentDataHolder componentData, ComponentPropertyDescriptor descriptor) {
        TypeDescriptor propertyType = descriptor.getPropertyType();

        if (propertyType instanceof TypeObjectDescriptor) {
            TypeObjectDescriptor typeObjectDescriptor = (TypeObjectDescriptor) propertyType;

            boolean shareable = typeObjectDescriptor.isShareable();

            JSONObject nestedJsonObject = parent.getJSONObject(descriptor.getPropertyName());

            TypeObjectDescriptor.TypeObject nestedObject = typeObjectDescriptor.newInstance();

            if (shareable) {

                // The object must contain a reference
                if (nestedJsonObject.has(JsonParser.Component.configRef())) {
                    String configRef = JsonParser.Component.configRef(nestedJsonObject);
                    nestedObject.set(JsonParser.Component.configRef(), configRef);
                    componentData.set(descriptor.getPropertyName(), nestedObject);
                } else {
                    throw new IllegalStateException("Expected config ref for @Shareable configuration");
                }

            } else {
                // The config is not shareable, hence we deserialize the object right away.
                typeObjectDescriptor.getObjectProperties()
                        .forEach(typeDescriptor -> deserialize(nestedJsonObject, nestedObject, typeDescriptor));
                componentData.set(descriptor.getPropertyName(), nestedObject);
            }

        } else {
            Object propertyValue = ValueConverterFactory
                    .forType(propertyType)
                    .from(descriptor.getPropertyName(), parent);
            componentData.set(descriptor.getPropertyName(), propertyValue);
        }
    }
}
