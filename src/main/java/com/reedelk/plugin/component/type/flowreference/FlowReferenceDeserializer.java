package com.reedelk.plugin.component.type.flowreference;

import com.reedelk.component.descriptor.ComponentData;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.deserializer.AbstractNodeDeserializer;
import com.reedelk.plugin.graph.deserializer.DeserializerContext;
import com.reedelk.plugin.graph.node.GraphNode;
import org.json.JSONObject;

import static com.reedelk.runtime.commons.JsonParser.FlowReference;
import static com.reedelk.runtime.commons.JsonParser.Implementor;

public class FlowReferenceDeserializer extends AbstractNodeDeserializer {

    public FlowReferenceDeserializer(FlowGraph graph, DeserializerContext context) {
        super(graph, context);
    }

    @Override
    public GraphNode deserialize(GraphNode parent, JSONObject componentDefinition) {

        String name = Implementor.name(componentDefinition);

        FlowReferenceNode node = context.instantiateGraphNode(name);

        ComponentData componentData = node.componentData();

        if (componentDefinition.has(FlowReference.ref())) {

            String reference = FlowReference.ref(componentDefinition);

            componentData.set(FlowReference.ref(), reference);
        }

        if (componentDefinition.has(Implementor.description())) {

            String description = Implementor.description(componentDefinition);

            componentData.set(Implementor.description(), description);
        }

        graph.add(parent, node);

        return node;
    }
}
