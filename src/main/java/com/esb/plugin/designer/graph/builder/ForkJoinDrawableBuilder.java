package com.esb.plugin.designer.graph.builder;

import com.esb.internal.commons.JsonParser;
import com.esb.plugin.designer.editor.component.ComponentDescriptor;
import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.drawable.Drawable;
import com.esb.plugin.designer.graph.drawable.ForkJoinDrawable;
import com.esb.plugin.designer.graph.drawable.StopDrawable;
import com.esb.plugin.service.module.ComponentService;
import com.intellij.openapi.module.Module;
import org.json.JSONArray;
import org.json.JSONObject;

public class ForkJoinDrawableBuilder implements Builder {

    @Override
    public Drawable build(Module module, Drawable parent, JSONObject componentDefinition, FlowGraph graph) {

        StopDrawable stopDrawable = new StopDrawable();

        String name = JsonParser.Implementor.name(componentDefinition);

        ComponentDescriptor forkComponent = ComponentService.getInstance(module).componentDescriptorByName(name);

        ForkJoinDrawable forkJoinDrawable = new ForkJoinDrawable(forkComponent);

        graph.add(parent, forkJoinDrawable);

        JSONArray fork = JsonParser.ForkJoin.getFork(componentDefinition);

        for (int i = 0; i < fork.length(); i++) {

            JSONObject next = fork.getJSONObject(i);
            JSONArray nextComponents = JsonParser.ForkJoin.getNext(next);

            Drawable currentDrawable = forkJoinDrawable;

            for (int j = 0; j < nextComponents.length(); j++) {
                JSONObject currentComponentDefinition = nextComponents.getJSONObject(j);
                currentDrawable = BuilderFactory.get(currentComponentDefinition)
                        .build(module, currentDrawable, currentComponentDefinition, graph);
            }

            graph.add(currentDrawable, stopDrawable);

        }

        JSONObject joinComponent = JsonParser.ForkJoin.getJoin(componentDefinition);
        return BuilderFactory.get(joinComponent).build(module, stopDrawable, joinComponent, graph);
    }

}
