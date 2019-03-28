package com.esb.plugin.designer.graph.builder;

import com.esb.internal.commons.JsonParser;
import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.drawable.Drawable;
import org.json.JSONArray;
import org.json.JSONObject;

import static com.google.common.base.Preconditions.checkArgument;

public class FlowGraphBuilder {

    private final JSONObject flowDefinition;
    private final FlowGraph flowGraph;

    public FlowGraphBuilder(String json) {
        checkArgument(json != null, "JSON must not be null");
        this.flowGraph = new FlowGraph();
        this.flowDefinition = JsonParser.from(json);
    }

    public FlowGraph graph() {
        JSONArray flow = JsonParser.Flow.getFlow(flowDefinition);

        Drawable current = null;
        for (int i = 0; i < flow.length(); i++) {
            JSONObject implementorDefinition = (JSONObject) flow.get(i);

            current = BuilderFactory
                    .get(implementorDefinition)
                    .build(current, implementorDefinition, flowGraph);

        }

        // TODO: Remove all stop drawables! When we display the graph we don't need it
        return flowGraph;
    }

}
