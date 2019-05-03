package com.esb.plugin.graph.deserializer;

import com.esb.internal.commons.FileUtils;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.FlowGraphImpl;
import com.esb.plugin.graph.node.GraphNode;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

import static com.esb.internal.commons.JsonParser.Flow;
import static com.esb.internal.commons.JsonParser.from;
import static com.google.common.base.Preconditions.checkArgument;

public class GraphDeserializer {

    private final FlowGraph graph;
    private final DeserializerContext context;
    private final JSONObject flowDefinition;

    GraphDeserializer(String json, DeserializerContext context) {
        checkArgument(json != null, "json");
        checkArgument(context != null, "context");

        this.context = context;
        this.graph = new FlowGraphImpl();
        this.flowDefinition = from(json);
    }

    protected FlowGraph deserialize() {
        JSONArray flow = Flow.getFlow(flowDefinition);

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

        return DeserializerUtilities.removeStopNodesFrom(graph);
    }

    public static Optional<FlowGraph> deserialize(Module module, VirtualFile file) {
        try {
            String json = FileUtils.readFrom(new URL(file.getUrl()));
            return deserialize(module, json);
        } catch (MalformedURLException e) {
            return Optional.empty();
        }
    }

    public static Optional<FlowGraph> deserialize(Module module, String json) {
        try {
            DeserializerContext context = new DeserializerContext(module);
            GraphDeserializer builder = new GraphDeserializer(json, context);
            FlowGraph graph = builder.deserialize();
            return Optional.of(graph);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
