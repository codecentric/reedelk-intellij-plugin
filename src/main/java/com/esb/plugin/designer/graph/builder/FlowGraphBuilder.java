package com.esb.plugin.designer.graph.builder;

import com.esb.internal.commons.JsonParser;
import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.Node;
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

    public FlowGraph get() {
        JSONArray flow = JsonParser.Flow.getFlow(flowDefinition);

        Node current = null;
        for (int i = 0; i < flow.length(); i++) {
            JSONObject implementorDefinition = (JSONObject) flow.get(i);

            current = HandlerFactory
                    .get(implementorDefinition)
                    .handle(current, implementorDefinition, flowGraph);

        }

        return flowGraph;
    }

}
