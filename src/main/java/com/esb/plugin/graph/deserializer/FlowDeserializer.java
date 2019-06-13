package com.esb.plugin.graph.deserializer;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.FlowGraphProvider;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Optional;

import static com.esb.internal.commons.JsonParser.Flow;

public class FlowDeserializer extends AbstractDeserializer {

    private static final String EMPTY_DESCRIPTION = "";
    private static final String EMPTY_TITLE = "";

    private static final Logger LOG = Logger.getInstance(FlowDeserializer.class);

    FlowDeserializer(String json, DeserializerContext context, FlowGraphProvider graphProvider) {
        super(json, context, graphProvider);
    }

    public static Optional<FlowGraph> deserialize(Module module, String json, FlowGraphProvider graphProvider) {
        DeserializerContext context = new DeserializerContext(module);
        FlowDeserializer deserializer = new FlowDeserializer(json, context, graphProvider);
        try {
            return Optional.of(deserializer.deserialize());
        } catch (Exception e) {
            LOG.error("Deserialization error", e);
            return Optional.empty();
        }
    }

    @Override
    protected String getId(JSONObject flowDefinition) {
        return Flow.id(flowDefinition);
    }

    @Override
    protected JSONArray getFlow(JSONObject flowDefinition) {
        return Flow.flow(flowDefinition);
    }

    @Override
    protected String getTitle(JSONObject flowDefinition) {
        return Flow.hasTitle(flowDefinition) ?
                Flow.title(flowDefinition) :
                EMPTY_TITLE;
    }

    @Override
    protected String getDescription(JSONObject flowDefinition) {
        return Flow.hasDescription(flowDefinition) ?
                Flow.description(flowDefinition) :
                EMPTY_DESCRIPTION;
    }
}
