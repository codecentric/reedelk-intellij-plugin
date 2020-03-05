package com.reedelk.plugin.component.type.foreach;

import com.reedelk.plugin.component.ComponentData;
import com.reedelk.plugin.component.deserializer.ComponentDataHolderDeserializer;
import com.reedelk.plugin.component.type.stop.StopNode;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.deserializer.AbstractNodeDeserializer;
import com.reedelk.plugin.graph.deserializer.DeserializerContext;
import com.reedelk.plugin.graph.deserializer.DeserializerFactory;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.graph.utils.CollectNodesBetween;
import com.reedelk.runtime.commons.JsonParser;
import com.reedelk.runtime.component.Stop;
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
                ComponentDataHolderDeserializer.deserialize(componentDefinition, componentData, descriptor));

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
