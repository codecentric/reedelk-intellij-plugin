package com.esb.plugin.designer.graph.builder;

import com.esb.internal.commons.JsonParser;
import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.FlowGraphImpl;
import com.esb.plugin.designer.graph.drawable.Drawable;
import com.intellij.openapi.module.Module;
import org.json.JSONArray;
import org.json.JSONObject;

import static com.google.common.base.Preconditions.checkArgument;

public class FlowGraphBuilder {

    private final FlowGraph graph;
    private final JSONObject flowDefinition;

    public FlowGraphBuilder(String json) {
        checkArgument(json != null, "JSON must not be null");
        this.graph = new FlowGraphImpl();
        this.flowDefinition = JsonParser.from(json);
    }

    public FlowGraph graph(Module module) {
        BuilderContext context = new BuilderContext(module);

        JSONArray flow = JsonParser.Flow.getFlow(flowDefinition);

        Drawable current = null;
        for (int i = 0; i < flow.length(); i++) {
            JSONObject implementorDefinition = (JSONObject) flow.get(i);
            current = DrawableBuilder.get()
                    .graph(graph)
                    .parent(current)
                    .context(context)
                    .componentDefinition(implementorDefinition)
                    .build();
        }

        return RemoveStopDrawables.from(graph);
    }
}
