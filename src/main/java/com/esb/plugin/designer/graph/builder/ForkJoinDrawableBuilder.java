package com.esb.plugin.designer.graph.builder;

import com.esb.internal.commons.JsonParser;
import com.esb.plugin.designer.editor.component.ComponentDescriptor;
import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.drawable.Drawable;
import com.esb.plugin.designer.graph.drawable.ForkJoinDrawable;
import com.esb.plugin.designer.graph.drawable.StopDrawable;
import org.json.JSONArray;
import org.json.JSONObject;

public class ForkJoinDrawableBuilder extends AbstractBuilder {

    ForkJoinDrawableBuilder(FlowGraph graph, BuilderContext context) {
        super(graph, context);
    }

    @Override
    public Drawable build(Drawable parent, JSONObject componentDefinition) {

        StopDrawable stopDrawable = new StopDrawable();

        String name = JsonParser.Implementor.name(componentDefinition);

        ComponentDescriptor forkComponent = context.instantiateComponent(name);

        ForkJoinDrawable forkJoinDrawable = new ForkJoinDrawable(forkComponent);

        graph.add(parent, forkJoinDrawable);

        JSONArray fork = JsonParser.ForkJoin.getFork(componentDefinition);

        for (int i = 0; i < fork.length(); i++) {

            JSONObject next = fork.getJSONObject(i);
            JSONArray nextComponents = JsonParser.ForkJoin.getNext(next);

            Drawable currentDrawable = forkJoinDrawable;

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
