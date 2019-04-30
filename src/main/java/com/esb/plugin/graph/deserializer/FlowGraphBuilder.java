package com.esb.plugin.graph.deserializer;

import com.esb.internal.commons.JsonParser;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.FlowGraphImpl;
import com.esb.plugin.graph.node.GraphNode;
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

            current = GraphNodeBuilder.get()
                    .graph(graph)
                    .parent(current)
                    .context(context)
                    .componentDefinition(implementorDefinition)
                    .build();
        }

        return StopNodesRemover.from(graph);
    }
}
