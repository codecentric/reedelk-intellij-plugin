package com.esb.plugin.graph.serializer;

import com.esb.plugin.graph.FlowGraph;
import org.json.JSONArray;
import org.json.JSONObject;

import static com.esb.internal.commons.JsonParser.Subflow;

public class SubFlowSerializer extends AbstractSerializer {

    public static String serialize(FlowGraph graph) {
        SubFlowSerializer serializer = new SubFlowSerializer(graph);
        return serializer.serialize();
    }

    private SubFlowSerializer(FlowGraph graph) {
        super(graph);
    }

    @Override
    protected String serialize() {
        JSONArray flow = new JSONArray();

        serializeFlow(flow);

        JSONObject flowObject = JsonObjectFactory.newJSONObject();
        Subflow.id(graph.id(), flowObject);
        Subflow.description(graph.description(), flowObject);
        Subflow.subflow(flow, flowObject);

        return flowObject.toString(JSON_INDENT_FACTOR);
    }
}
