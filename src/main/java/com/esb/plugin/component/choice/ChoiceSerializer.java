package com.esb.plugin.component.choice;

import com.esb.plugin.component.Component;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.serializer.AbstractSerializer;
import com.esb.plugin.graph.serializer.SerializerUtilities;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import static com.esb.internal.commons.JsonParser.Implementor;

public class ChoiceSerializer extends AbstractSerializer {
    /**
     * {
     * "implementor.name": "com.esb.component.Choice",
     * "when": [
     * {
     * "condition": "payload.name == 35",
     * "next": [
     * {
     * "implementor.name": "com.esb.core.component.SetPayload1",
     * "payload": "{\"result\":\"Payload contains 35\"}"
     * }
     * ]
     * },
     * {
     * "condition": "payload.name == 'Mark'",
     * "next": [
     * {
     * "implementor.name": "com.esb.core.component.SetPayload2",
     * "payload": "{\"result\":\"Choice2\"}"
     * }
     * ]
     * }
     * ],
     * "otherwise": [
     * {
     * "implementor.name": "com.esb.core.component.SetPayload3",
     * "payload": "{\"result\":\"Otherwise\"}"
     * }
     * ]
     * }
     */
    @Override
    public JSONObject serialize(GraphNode node) {
        Component component = node.component();

        JSONObject choice = SerializerUtilities.newJSONObject();

        Implementor.name(component.getFullyQualifiedName(), choice);

        List<GraphNode> when = (List<GraphNode>) component.getData("when");

        JSONArray whenArray = new JSONArray();


        choice.put("when", whenArray);

        return choice;
    }
}
