package com.reedelk.plugin.component.type.fork;

import com.reedelk.plugin.component.domain.ComponentData;
import com.reedelk.plugin.component.type.stop.StopNode;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.deserializer.AbstractNodeDeserializer;
import com.reedelk.plugin.graph.deserializer.DeserializerContext;
import com.reedelk.plugin.graph.deserializer.DeserializerFactory;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.graph.utils.CollectNodesBetween;
import com.reedelk.runtime.component.Stop;
import org.json.JSONArray;
import org.json.JSONObject;

import static com.reedelk.runtime.commons.JsonParser.Fork;
import static com.reedelk.runtime.commons.JsonParser.Implementor;

public class ForkDeserializer extends AbstractNodeDeserializer {

    public ForkDeserializer(FlowGraph graph, DeserializerContext context) {
        super(graph, context);
    }

    @Override
    public GraphNode deserialize(GraphNode parent, JSONObject componentDefinition) {

        StopNode stopNode = context.instantiateGraphNode(Stop.class.getName());

        String name = Implementor.name(componentDefinition);

        ForkNode forkNode = context.instantiateGraphNode(name);

        ComponentData componentData = forkNode.componentData();

        // Set thread pool size
        if (componentDefinition.has(Fork.threadPoolSize())) {
            Integer threadPoolSize = Fork.threadPoolSize(componentDefinition);
            componentData.set(Fork.threadPoolSize(), threadPoolSize);
        }

        graph.add(parent, forkNode);

        JSONArray fork = Fork.fork(componentDefinition);

        // If the fork does not contain any branch, we immediately stop.
        if (fork.isEmpty()) {
            graph.add(forkNode, stopNode);
            return stopNode;
        }

        for (int i = 0; i < fork.length(); i++) {
            JSONObject next = fork.getJSONObject(i);
            JSONArray nextComponents = Fork.next(next);

            GraphNode currentNode = forkNode;
            for (int j = 0; j < nextComponents.length(); j++) {
                JSONObject currentComponentDefinition = nextComponents.getJSONObject(j);
                currentNode = DeserializerFactory.get()
                        .componentDefinition(currentComponentDefinition)
                        .context(context)
                        .graph(graph)
                        .build()
                        .deserialize(currentNode, currentComponentDefinition);
            }

            graph.add(currentNode, stopNode);
        }

        // We must add all the nodes between fork and the stop node
        // to the fork's scope.
        CollectNodesBetween
                .them(graph, forkNode, stopNode)
                .forEach(forkNode::addToScope);

        return stopNode;
    }
}
