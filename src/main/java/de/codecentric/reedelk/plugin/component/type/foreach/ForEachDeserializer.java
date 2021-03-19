package de.codecentric.reedelk.plugin.component.type.foreach;

import de.codecentric.reedelk.plugin.component.ComponentData;
import de.codecentric.reedelk.plugin.component.deserializer.ComponentDataHolderDeserializer;
import de.codecentric.reedelk.plugin.component.type.stop.StopNode;
import de.codecentric.reedelk.plugin.graph.FlowGraph;
import de.codecentric.reedelk.plugin.graph.deserializer.AbstractNodeDeserializer;
import de.codecentric.reedelk.plugin.graph.deserializer.DeserializerContext;
import de.codecentric.reedelk.plugin.graph.deserializer.DeserializerFactory;
import de.codecentric.reedelk.plugin.graph.node.GraphNode;
import de.codecentric.reedelk.plugin.graph.utils.CollectNodesBetween;
import de.codecentric.reedelk.runtime.commons.JsonParser;
import de.codecentric.reedelk.runtime.component.Stop;
import org.json.JSONArray;
import org.json.JSONObject;

public class ForEachDeserializer extends AbstractNodeDeserializer {

    public ForEachDeserializer(FlowGraph graph, GraphNode current, DeserializerContext context) {
        super(graph, current, context);
    }

    @Override
    public GraphNode deserialize(GraphNode parent, JSONObject componentDefinition) {
        StopNode stopNode = context.instantiateGraphNode(Stop.class.getName());

        ComponentData componentData = current.componentData();

        componentData.getPropertiesDescriptors().forEach(descriptor ->
                ComponentDataHolderDeserializer.get().deserialize(componentData, descriptor, componentDefinition));

        ForEachNode forEachNode = (ForEachNode) current;

        graph.add(parent, forEachNode);

        JSONArray forEach = JsonParser.ForEach.next(componentDefinition);

        // If the for each does not contain any branch, we immediately stop.
        if (forEach.isEmpty()) {
            graph.add(forEachNode, stopNode);
            return stopNode;
        }

        GraphNode currentNode = forEachNode;
        for (int i = 0; i < forEach.length(); i++) {

            JSONObject currentComponentDefinition = forEach.getJSONObject(i);

            currentNode = DeserializerFactory.get()
                    .componentDefinition(currentComponentDefinition)
                    .context(context)
                    .graph(graph)
                    .build()
                    .deserialize(currentNode, currentComponentDefinition);
        }

        graph.add(currentNode, stopNode);

        // We must add all the nodes between fork and
        // the stop node to the fork's scope.
        CollectNodesBetween
                .them(graph, forEachNode, stopNode)
                .forEach(forEachNode::addToScope);

        return stopNode;
    }
}
