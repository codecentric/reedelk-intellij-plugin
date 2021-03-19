package de.codecentric.reedelk.plugin.graph.serializer;

import de.codecentric.reedelk.plugin.graph.FlowGraph;
import de.codecentric.reedelk.plugin.commons.JsonObjectFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import static de.codecentric.reedelk.runtime.commons.JsonParser.Subflow;

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
        Subflow.title(graph.title(), flowObject);
        Subflow.description(graph.description(), flowObject);
        Subflow.subflow(flow, flowObject);

        return flowObject.toString(JSON_INDENT_FACTOR);
    }
}
