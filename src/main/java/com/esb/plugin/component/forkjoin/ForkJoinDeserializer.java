package com.esb.plugin.component.forkjoin;

import com.esb.component.Stop;
import com.esb.internal.commons.JsonParser;
import com.esb.plugin.component.stop.StopNode;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.deserializer.AbstractDeserializer;
import com.esb.plugin.graph.deserializer.DeserializerContext;
import com.esb.plugin.graph.deserializer.GraphNodeDeserializerFactory;
import com.esb.plugin.graph.node.GraphNode;
import org.json.JSONArray;
import org.json.JSONObject;

public class ForkJoinDeserializer extends AbstractDeserializer {

    public ForkJoinDeserializer(FlowGraph graph, DeserializerContext context) {
        super(graph, context);
    }

    @Override
    public GraphNode deserialize(GraphNode parent, JSONObject componentDefinition) {

        StopNode stopDrawable = context.instantiateGraphNode(Stop.class.getName());

        String name = JsonParser.Implementor.name(componentDefinition);

        ForkJoinNode forkJoinDrawable = context.instantiateGraphNode(name);

        graph.add(parent, forkJoinDrawable);

        JSONArray fork = JsonParser.ForkJoin.getFork(componentDefinition);

        for (int i = 0; i < fork.length(); i++) {

            JSONObject next = fork.getJSONObject(i);
            JSONArray nextComponents = JsonParser.ForkJoin.getNext(next);

            GraphNode currentDrawable = forkJoinDrawable;

            for (int j = 0; j < nextComponents.length(); j++) {
                JSONObject currentComponentDefinition = nextComponents.getJSONObject(j);
                currentDrawable = GraphNodeDeserializerFactory.get()
                        .componentDefinition(currentComponentDefinition)
                        .context(context)
                        .graph(graph)
                        .parent(currentDrawable)
                        .build();
            }

            graph.add(currentDrawable, stopDrawable);

        }

        JSONObject joinComponent = JsonParser.ForkJoin.getJoin(componentDefinition);
        return GraphNodeDeserializerFactory.get()
                .componentDefinition(joinComponent)
                .parent(stopDrawable)
                .context(context)
                .graph(graph)
                .build();
    }

}
