package com.reedelk.plugin.component.type.generic;

import com.reedelk.plugin.component.domain.*;
import com.reedelk.plugin.converter.ValueConverterFactory;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.deserializer.AbstractNodeDeserializer;
import com.reedelk.plugin.graph.deserializer.DeserializerContext;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.runtime.commons.JsonParser;
import org.json.JSONObject;

import static com.reedelk.runtime.commons.JsonParser.Implementor;

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

        // TODO: This is wrong. Should just deserialize all the  properties from the definition,
        //  TODO:  if there is no match in the json, then nothing happens...
        jsonDefinition.keys().forEachRemaining(propertyName ->
                componentData.getPropertyDescriptor(propertyName)
                        .ifPresent(descriptor -> deserialize(jsonDefinition, componentData, descriptor)));

        graph.add(parent, node);

        return node;
    }

    public static void deserialize(JSONObject parent, ComponentDataHolder componentData, ComponentPropertyDescriptor descriptor) {
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
