package com.reedelk.plugin.component.type.generic;

import com.reedelk.plugin.component.ComponentData;
import com.reedelk.plugin.component.deserializer.ComponentDataHolderDeserializer;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.deserializer.AbstractNodeDeserializer;
import com.reedelk.plugin.graph.deserializer.DeserializerContext;
import com.reedelk.plugin.graph.node.GraphNode;
import org.json.JSONObject;

public class GenericComponentDeserializer extends AbstractNodeDeserializer {

    public GenericComponentDeserializer(FlowGraph graph, GraphNode current, DeserializerContext context) {
        super(graph, current, context);
    }

    @Override
    public GraphNode deserialize(GraphNode parent, JSONObject jsonDefinition) {

        ComponentData componentData = current.componentData();

        componentData.getPropertiesDescriptors().forEach(descriptor ->
                        ComponentDataHolderDeserializer.get().deserialize(jsonDefinition, componentData, descriptor));

        graph.add(parent, current);

        return current;
    }
}
