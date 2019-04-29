package com.esb.plugin.designer.graph.deserializer;

import com.esb.internal.commons.JsonParser;
import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.FlowGraphImpl;
import com.esb.plugin.designer.graph.GraphNode;
import org.json.JSONArray;
import org.json.JSONObject;

import static com.google.common.base.Preconditions.checkArgument;

public class FlowGraphBuilder {

    private final FlowGraph graph;
    private final BuilderContext context;
    private final JSONObject flowDefinition;

    FlowGraphBuilder(String json, BuilderContext context) {
        checkArgument(json != null, "JSON");
        checkArgument(context != null, "BuilderContext");

        this.context = context;
        this.graph = new FlowGraphImpl();
        this.flowDefinition = JsonParser.from(json);
    }

    public FlowGraph graph() {
        JSONArray flow = JsonParser.Flow.getFlow(flowDefinition);

        GraphNode current = null;

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
