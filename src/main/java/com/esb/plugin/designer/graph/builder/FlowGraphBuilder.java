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

    private final JSONObject flowDefinition;
    private final FlowGraph graph;

    public FlowGraphBuilder(String json) {
        checkArgument(json != null, "JSON must not be null");
        this.graph = new FlowGraphImpl();
        this.flowDefinition = JsonParser.from(json);
    }

    public FlowGraph graph(Module module) {
        JSONArray flow = JsonParser.Flow.getFlow(flowDefinition);

        Drawable current = null;
        for (int i = 0; i < flow.length(); i++) {
            JSONObject implementorDefinition = (JSONObject) flow.get(i);
            current = BuilderFactory
                    .get(implementorDefinition)
                    .build(module, current, implementorDefinition, graph);
        }

        return RemoveStopDrawables.from(graph);
    }
}
