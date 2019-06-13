package com.esb.plugin.graph.deserializer;

import com.esb.internal.commons.JsonParser;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.FlowGraphProvider;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Optional;

public class SubFlowDeserializer extends AbstractDeserializer {

    private static final Logger LOG = Logger.getInstance(FlowDeserializer.class);

    private SubFlowDeserializer(String json, DeserializerContext context, FlowGraphProvider graphProvider) {
        super(json, context, graphProvider);
    }

    public static Optional<FlowGraph> deserialize(Module module, String json, FlowGraphProvider graphProvider) {
        DeserializerContext context = new DeserializerContext(module);
        SubFlowDeserializer deserializer = new SubFlowDeserializer(json, context, graphProvider);
        try {
            return Optional.of(deserializer.deserialize());
        } catch (Exception e) {
            LOG.error("Deserialization error", e);
            return Optional.empty();
        }
    }

    @Override
    protected JSONArray getFlow(JSONObject flowDefinition) {
        return JsonParser.Subflow.subflow(flowDefinition);
    }
}
