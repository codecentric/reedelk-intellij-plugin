package com.esb.plugin.graph.serializer;

import com.esb.plugin.graph.FlowGraph;
import org.json.JSONArray;
import org.json.JSONObject;

import static com.esb.internal.commons.JsonParser.Flow;

public class FlowSerializer extends AbstractSerializer {

    public static String serialize(FlowGraph graph) {
        FlowSerializer serializer = new FlowSerializer(graph);
        return serializer.serialize();
    }

    private FlowSerializer(FlowGraph graph) {
        super(graph);
    }

    @Override
    protected String serialize() {
        JSONArray flow = new JSONArray();

        serializeFlow(flow);

        JSONObject flowObject = JsonObjectFactory.newJSONObject();
        Flow.id(graph.id(), flowObject);
        Flow.title(graph.title(), flowObject);
        Flow.description(graph.description(), flowObject);
        Flow.flow(flow, flowObject);

        return flowObject.toString(JSON_INDENT_FACTOR);
    }
}
