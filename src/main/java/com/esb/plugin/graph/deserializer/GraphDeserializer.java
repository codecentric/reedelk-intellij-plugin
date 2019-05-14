package com.esb.plugin.graph.deserializer;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.FlowGraphProvider;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.utils.RemoveStopNodes;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Optional;

import static com.esb.internal.commons.JsonParser.Flow;
import static com.esb.internal.commons.JsonParser.from;
import static com.google.common.base.Preconditions.checkArgument;

public class GraphDeserializer {

    private static final Logger LOG = Logger.getInstance(GraphDeserializer.class);

    private final JSONObject flowDefinition;
    private final DeserializerContext context;
    private final FlowGraphProvider graphProvider;

    public static Optional<FlowGraph> deserialize(Module module, String json, FlowGraphProvider graphProvider) {
        DeserializerContext context = new DeserializerContext(module);
        GraphDeserializer deserializer = new GraphDeserializer(json, context, graphProvider);
        try {
            return Optional.of(deserializer.deserialize());
        } catch (Exception e) {
            LOG.error("Deserialization error", e);
            return Optional.empty();
        }
    }

    GraphDeserializer(String json, DeserializerContext context, FlowGraphProvider graphProvider) {
        checkArgument(json != null, "json");
        checkArgument(context != null, "context");
        checkArgument(graphProvider != null, "graph provider");

        this.context = context;
        this.flowDefinition = from(json);
        this.graphProvider = graphProvider;
    }

    FlowGraph deserialize() {
        JSONArray flow = Flow.flow(flowDefinition);
        FlowGraph graph = graphProvider.createGraph(Flow.id(flowDefinition));

        GraphNode current = null;
        for (int i = 0; i < flow.length(); i++) {
            JSONObject implementorDefinition = (JSONObject) flow.get(i);

            current = GraphDeserializerFactory.get()
                    .componentDefinition(implementorDefinition)
                    .context(context)
                    .graph(graph)
                    .build()
                    .deserialize(current, implementorDefinition);
        }

        return RemoveStopNodes.from(graph);
    }

}
