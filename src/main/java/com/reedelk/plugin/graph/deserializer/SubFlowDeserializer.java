package com.reedelk.plugin.graph.deserializer;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.FlowGraphProvider;
import com.reedelk.runtime.api.commons.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import static com.reedelk.runtime.commons.JsonParser.Subflow;

public class SubFlowDeserializer extends AbstractDeserializer {

    private static final String EMPTY_DESCRIPTION = StringUtils.EMPTY;
    private static final String EMPTY_TITLE = StringUtils.EMPTY;

    private SubFlowDeserializer(String json, DeserializerContext context, FlowGraphProvider graphProvider) {
        super(json, context, graphProvider);
    }

    public static FlowGraph deserialize(Module module, String json, FlowGraphProvider graphProvider) throws DeserializationError {
        DeserializerContext context = new DeserializerContext(module);
        SubFlowDeserializer deserializer = new SubFlowDeserializer(json, context, graphProvider);
        try {
            return deserializer.deserialize();
        } catch (Exception e) {
            throw new DeserializationError(e);
        }
    }

    @Override
    protected JSONArray getFlow(JSONObject flowDefinition) {
        return Subflow.subflow(flowDefinition);
    }

    @Override
    protected String getId(JSONObject flowDefinition) {
        return Subflow.id(flowDefinition);
    }

    @Override
    protected String getTitle(JSONObject flowDefinition) {
        return Subflow.hasTitle(flowDefinition) ?
                Subflow.title(flowDefinition) :
                EMPTY_TITLE;
    }

    @Override
    protected String getDescription(JSONObject flowDefinition) {
        return Subflow.hasDescription(flowDefinition) ?
                Subflow.description(flowDefinition) :
                EMPTY_DESCRIPTION;
    }
}
