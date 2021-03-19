package de.codecentric.reedelk.plugin.component.type.generic;

import de.codecentric.reedelk.module.descriptor.model.property.PropertyDescriptor;
import de.codecentric.reedelk.plugin.component.ComponentData;
import de.codecentric.reedelk.plugin.component.deserializer.ComponentDataHolderDeserializer;
import de.codecentric.reedelk.plugin.graph.FlowGraph;
import de.codecentric.reedelk.plugin.graph.deserializer.AbstractNodeDeserializer;
import de.codecentric.reedelk.plugin.graph.deserializer.DeserializerContext;
import de.codecentric.reedelk.plugin.graph.node.GraphNode;
import org.json.JSONObject;

import java.util.List;

public class GenericComponentDeserializer extends AbstractNodeDeserializer {

    public GenericComponentDeserializer(FlowGraph graph, GraphNode current, DeserializerContext context) {
        super(graph, current, context);
    }

    @Override
    public GraphNode deserialize(GraphNode parent, JSONObject jsonObject) {

        ComponentData componentData = current.componentData();

        List<PropertyDescriptor> descriptors = componentData.getPropertiesDescriptors();

        descriptors.forEach(propertyDescriptor ->
                ComponentDataHolderDeserializer.get().deserialize(componentData, propertyDescriptor, jsonObject));

        graph.add(parent, current);

        return current;
    }
}
