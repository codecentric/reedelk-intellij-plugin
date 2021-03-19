package de.codecentric.reedelk.plugin.component.type.trycatch;

import de.codecentric.reedelk.plugin.component.type.stop.StopNode;
import de.codecentric.reedelk.plugin.graph.FlowGraph;
import de.codecentric.reedelk.plugin.graph.deserializer.AbstractNodeDeserializer;
import de.codecentric.reedelk.plugin.graph.deserializer.DeserializerContext;
import de.codecentric.reedelk.plugin.graph.deserializer.DeserializerFactory;
import de.codecentric.reedelk.plugin.graph.node.GraphNode;
import de.codecentric.reedelk.plugin.graph.utils.CollectNodesBetween;
import de.codecentric.reedelk.runtime.component.Stop;
import org.json.JSONArray;
import org.json.JSONObject;

import static de.codecentric.reedelk.runtime.commons.JsonParser.Implementor;
import static de.codecentric.reedelk.runtime.commons.JsonParser.TryCatch;

public class TryCatchDeserializer extends AbstractNodeDeserializer {

    public TryCatchDeserializer(FlowGraph graph, GraphNode current, DeserializerContext context) {
        super(graph, current, context);
    }

    @Override
    public GraphNode deserialize(GraphNode parent, JSONObject componentDefinition) {

        StopNode stopNode = context.instantiateGraphNode(Stop.class.getName());

        String name = Implementor.name(componentDefinition);

        TryCatchNode tryCatchNode = context.instantiateGraphNode(name);

        graph.add(parent, tryCatchNode);

        JSONArray tryComponents = TryCatch.doTry(componentDefinition);

        // If the try does not contain any branch, we immediately stop.
        if (tryComponents.isEmpty()) {
            graph.add(tryCatchNode, stopNode);
            return stopNode;
        }

        GraphNode currentNode = tryCatchNode;
        for (int i = 0; i < tryComponents.length(); i++) {
            JSONObject currentComponentDefinition = tryComponents.getJSONObject(i);
            currentNode = DeserializerFactory.get()
                    .componentDefinition(currentComponentDefinition)
                    .context(context)
                    .graph(graph)
                    .build()
                    .deserialize(currentNode, currentComponentDefinition);

        }
        graph.add(currentNode, stopNode);

        JSONArray catchComponents = TryCatch.doCatch(componentDefinition);
        currentNode = tryCatchNode;
        for (int i = 0; i < catchComponents.length(); i++) {
            JSONObject currentComponentDefinition = catchComponents.getJSONObject(i);
            currentNode = DeserializerFactory.get()
                    .componentDefinition(currentComponentDefinition)
                    .context(context)
                    .graph(graph)
                    .build()
                    .deserialize(currentNode, currentComponentDefinition);

        }
        graph.add(currentNode, stopNode);

        // We must add all the nodes between fork and the stop node
        // to the fork's scope.
        CollectNodesBetween
                .them(graph, tryCatchNode, stopNode)
                .forEach(tryCatchNode::addToScope);

        return stopNode;
    }
}
