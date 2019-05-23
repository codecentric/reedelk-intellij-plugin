package com.esb.plugin.component.type.fork;

import com.esb.plugin.component.type.stop.StopNode;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.deserializer.AbstractDeserializer;
import com.esb.plugin.graph.deserializer.DeserializerContext;
import com.esb.plugin.graph.deserializer.GraphDeserializerFactory;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.utils.CollectNodesBetween;
import com.esb.system.component.Stop;
import org.json.JSONArray;
import org.json.JSONObject;

import static com.esb.internal.commons.JsonParser.Fork;
import static com.esb.internal.commons.JsonParser.Implementor;

public class ForkDeserializer extends AbstractDeserializer {

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

        for (int i = 0; i < fork.length(); i++) {
            JSONObject next = fork.getJSONObject(i);
            JSONArray nextComponents = Fork.next(next);

            GraphNode currentDrawable = forkNode;
            for (int j = 0; j < nextComponents.length(); j++) {
                JSONObject currentComponentDefinition = nextComponents.getJSONObject(j);
                currentDrawable = GraphDeserializerFactory.get()
                        .componentDefinition(currentComponentDefinition)
                        .context(context)
                        .graph(graph)
                        .build()
                        .deserialize(currentDrawable, currentComponentDefinition);
            }

            graph.add(currentDrawable, stopNode);

        }

        CollectNodesBetween
                .them(graph, forkNode, stopNode)
                .forEach(forkNode::addToScope);

        return stopNode;

    }

}
