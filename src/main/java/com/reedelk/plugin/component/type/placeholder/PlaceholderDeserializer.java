package com.reedelk.plugin.component.type.placeholder;

import com.reedelk.plugin.component.ComponentData;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.deserializer.AbstractNodeDeserializer;
import com.reedelk.plugin.graph.deserializer.DeserializerContext;
import com.reedelk.plugin.graph.node.GraphNode;
import org.json.JSONObject;

import static com.reedelk.runtime.commons.JsonParser.Implementor;

public class PlaceholderDeserializer extends AbstractNodeDeserializer {

    public PlaceholderDeserializer(FlowGraph graph, GraphNode current, DeserializerContext context) {
        super(graph, current, context);
    }

    @Override
    public GraphNode deserialize(GraphNode parent, JSONObject jsonDefinition) {

        ComponentData componentData = current.componentData();

        if (jsonDefinition.has(Implementor.description())) {
            componentData.set(Implementor.description(), Implementor.description(jsonDefinition));
        }

        graph.add(parent, current);

        return current;
    }
}
