package com.esb.plugin.graph.deserializer;

import com.esb.internal.commons.JsonParser;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.FlowGraphProvider;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.utils.RemoveStopNodes;
import org.json.JSONArray;
import org.json.JSONObject;

import static com.esb.internal.commons.JsonParser.from;
import static com.google.common.base.Preconditions.checkArgument;

abstract class AbstractDeserializer {

    private final String json;
    private final DeserializerContext context;
    private final FlowGraphProvider graphProvider;


    AbstractDeserializer(String json, DeserializerContext context, FlowGraphProvider graphProvider) {
        checkArgument(json != null, "json");
        checkArgument(context != null, "context");
        checkArgument(graphProvider != null, "graph provider");
        this.json = json;
        this.context = context;
        this.graphProvider = graphProvider;
    }

    FlowGraph deserialize() {
        JSONObject flowDefinition = from(json);
        JSONArray flow = getFlow(flowDefinition);
        FlowGraph graph = graphProvider.createGraph(JsonParser.Flow.id(flowDefinition));

        GraphNode current = null;
        for (int i = 0; i < flow.length(); i++) {
            JSONObject implementorDefinition = (JSONObject) flow.get(i);

            current = FlowDeserializerFactory.get()
                    .componentDefinition(implementorDefinition)
                    .context(context)
                    .graph(graph)
                    .build()
                    .deserialize(current, implementorDefinition);
        }

        return RemoveStopNodes.from(graph);
    }

    protected abstract JSONArray getFlow(JSONObject flowDefinition);

}
