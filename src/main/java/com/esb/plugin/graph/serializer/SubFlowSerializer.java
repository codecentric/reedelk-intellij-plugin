package com.esb.plugin.graph.serializer;

import com.esb.internal.commons.JsonParser;
import com.esb.plugin.graph.FlowGraph;
import org.json.JSONArray;
import org.json.JSONObject;

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
        JsonParser.Subflow.id(graph.id(), flowObject);
        JsonParser.Subflow.subflow(flow, flowObject);
        return flowObject.toString(2);
    }
}
