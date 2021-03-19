package de.codecentric.reedelk.plugin.component.type.unknown;

import de.codecentric.reedelk.plugin.component.ComponentData;
import de.codecentric.reedelk.plugin.graph.FlowGraph;
import de.codecentric.reedelk.plugin.graph.deserializer.AbstractNodeDeserializer;
import de.codecentric.reedelk.plugin.graph.deserializer.DeserializerContext;
import de.codecentric.reedelk.plugin.graph.node.GraphNode;
import org.json.JSONObject;

public class UnknownDeserializer extends AbstractNodeDeserializer {

    public UnknownDeserializer(FlowGraph graph, GraphNode current, DeserializerContext context) {
        super(graph, current, context);
    }

    @Override
    public GraphNode deserialize(GraphNode parent, JSONObject componentDefinition) {

        ComponentData componentData = current.componentData();

        // If the component is not known, we must copy all the properties in order
        // to write all of them back when the object is serialized.
        componentDefinition.keySet()
                .forEach(key -> componentData.set(key, componentDefinition.get(key)));

        graph.add(parent, current);

        return current;
    }
}
