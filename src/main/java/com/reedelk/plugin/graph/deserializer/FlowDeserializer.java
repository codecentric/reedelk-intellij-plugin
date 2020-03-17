package com.reedelk.plugin.graph.deserializer;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.FlowGraphProvider;
import com.reedelk.runtime.api.commons.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import static com.reedelk.runtime.commons.JsonParser.Flow;

public class FlowDeserializer extends AbstractDeserializer {

    private static final String EMPTY_DESCRIPTION = StringUtils.EMPTY;
    private static final String EMPTY_TITLE = StringUtils.EMPTY;

    FlowDeserializer(String json, DeserializerContext context, FlowGraphProvider graphProvider) {
        super(json, context, graphProvider);
    }

    @NotNull
    public static FlowGraph deserialize(Module module, String json, FlowGraphProvider graphProvider) throws DeserializationError {
        DeserializerContext context = new DeserializerContext(module);
        FlowDeserializer deserializer = new FlowDeserializer(json, context, graphProvider);
        try {
            return deserializer.deserialize();
        } catch (Exception exception) {
            throw new DeserializationError(exception);
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
