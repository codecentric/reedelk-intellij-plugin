package com.esb.plugin.component.forkjoin;

import com.esb.internal.commons.JsonParser;
import com.esb.plugin.component.Component;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.GraphNode;
import com.esb.plugin.graph.deserializer.AbstractBuilder;
import com.esb.plugin.graph.deserializer.BuilderContext;
import com.esb.plugin.graph.deserializer.DrawableBuilder;
import com.esb.plugin.graph.drawable.StopGraphNode;
import org.json.JSONArray;
import org.json.JSONObject;

public class ForkJoinGraphNodeBuilder extends AbstractBuilder {

    public ForkJoinGraphNodeBuilder(FlowGraph graph, BuilderContext context) {
        super(graph, context);
    }

    @Override
    public GraphNode build(GraphNode parent, JSONObject componentDefinition) {

        StopGraphNode stopDrawable = new StopGraphNode();

        String name = JsonParser.Implementor.name(componentDefinition);

        Component forkComponent = context.instantiateComponent(name);

        ForkJoinGraphNode forkJoinDrawable = new ForkJoinGraphNode(forkComponent);

        graph.add(parent, forkJoinDrawable);

        JSONArray fork = JsonParser.ForkJoin.getFork(componentDefinition);

        for (int i = 0; i < fork.length(); i++) {

            JSONObject next = fork.getJSONObject(i);
            JSONArray nextComponents = JsonParser.ForkJoin.getNext(next);

            GraphNode currentDrawable = forkJoinDrawable;

            for (int j = 0; j < nextComponents.length(); j++) {
                JSONObject currentComponentDefinition = nextComponents.getJSONObject(j);
                currentDrawable = DrawableBuilder.get()
                        .componentDefinition(currentComponentDefinition)
                        .context(context)
                        .graph(graph)
                        .parent(currentDrawable)
                        .build();
            }

            graph.add(currentDrawable, stopDrawable);

        }

        JSONObject joinComponent = JsonParser.ForkJoin.getJoin(componentDefinition);
        return DrawableBuilder.get()
                .componentDefinition(joinComponent)
                .parent(stopDrawable)
                .context(context)
                .graph(graph)
                .build();
    }

}
