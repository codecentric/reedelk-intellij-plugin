package com.reedelk.plugin.component.type.flowreference;

import com.reedelk.plugin.component.ComponentData;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.deserializer.AbstractNodeDeserializer;
import com.reedelk.plugin.graph.deserializer.DeserializerContext;
import com.reedelk.plugin.graph.node.GraphNode;
import org.json.JSONObject;

import static com.reedelk.runtime.commons.JsonParser.FlowReference;
import static com.reedelk.runtime.commons.JsonParser.Implementor;

public class FlowReferenceDeserializer extends AbstractNodeDeserializer {

    public FlowReferenceDeserializer(FlowGraph graph, GraphNode current, DeserializerContext context) {
        super(graph, current, context);
    }

    @Override
    public GraphNode deserialize(GraphNode parent, JSONObject componentDefinition) {

        FlowReferenceNode node = (FlowReferenceNode) current;

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
