package com.esb.plugin.component.type.fork;

import com.esb.plugin.component.type.stop.StopNode;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.deserializer.AbstractNodeDeserializer;
import com.esb.plugin.graph.deserializer.DeserializerContext;
import com.esb.plugin.graph.deserializer.FlowDeserializerFactory;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.utils.CollectNodesBetween;
import com.esb.system.component.Stop;
import org.json.JSONArray;
import org.json.JSONObject;

import static com.esb.internal.commons.JsonParser.Fork;
import static com.esb.internal.commons.JsonParser.Implementor;

public class ForkDeserializer extends AbstractNodeDeserializer {

    public ForkDeserializer(FlowGraph graph, DeserializerContext context) {
        super(graph, context);
    }

    @Override
    public GraphNode deserialize(GraphNode parent, JSONObject componentDefinition) {

        StopNode stopNode = context.instantiateGraphNode(Stop.class.getName());

        String name = Implementor.name(componentDefinition);

        ForkNode forkNode = context.instantiateGraphNode(name);

        int threadPoolSize = Fork.threadPoolSize(componentDefinition);

        forkNode.componentData().set(Fork.threadPoolSize(), threadPoolSize);

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
                currentNode = FlowDeserializerFactory.get()
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
