package com.reedelk.plugin.component.type.generic;

import com.reedelk.module.descriptor.model.PropertyDescriptor;
import com.reedelk.plugin.component.ComponentData;
import com.reedelk.plugin.component.deserializer.ComponentDataHolderDeserializer;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.deserializer.AbstractNodeDeserializer;
import com.reedelk.plugin.graph.deserializer.DeserializerContext;
import com.reedelk.plugin.graph.node.GraphNode;
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
