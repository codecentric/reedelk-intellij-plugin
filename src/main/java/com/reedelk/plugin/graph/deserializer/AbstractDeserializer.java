package com.reedelk.plugin.graph.deserializer;

import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.FlowGraphProvider;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.graph.utils.RemoveStopNodes;
import org.json.JSONArray;
import org.json.JSONObject;

import static com.google.common.base.Preconditions.checkArgument;
import static com.reedelk.runtime.commons.JsonParser.from;

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

        String id = getId(flowDefinition);
        String title = getTitle(flowDefinition);
        String description = getDescription(flowDefinition);

        FlowGraph graph = graphProvider.createGraph(id);
        graph.setDescription(description);
        graph.setTitle(title);

        GraphNode current = null;
        for (int i = 0; i < flow.length(); i++) {
            JSONObject implementorDefinition = (JSONObject) flow.get(i);

            current = DeserializerFactory.get()
                    .componentDefinition(implementorDefinition)
                    .context(context)
                    .graph(graph)
                    .build()
                    .deserialize(current, implementorDefinition);
        }

        return RemoveStopNodes.from(graph);
    }

    protected abstract String getId(JSONObject flowDefinition);

    protected abstract String getTitle(JSONObject flowDefinition);

    protected abstract String getDescription(JSONObject description);

    protected abstract JSONArray getFlow(JSONObject flowDefinition);

}
