package com.esb.plugin.component.choice;

import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.serializer.AbstractSerializer;
import com.esb.plugin.graph.serializer.SerializerUtilities;
import org.json.JSONObject;

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
        JSONObject choice = SerializerUtilities.newJSONObject();



        return choice;
    }
}
