package com.esb.plugin.component.choice;

import com.esb.plugin.component.ComponentData;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.serializer.AbstractSerializer;
import com.esb.plugin.graph.serializer.GraphSerializer;
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
    public JSONObject serialize(FlowGraph graph, GraphNode node) {
        ComponentData componentData = node.component();

        JSONObject choice = SerializerUtilities.newJSONObject();

        Implementor.name(componentData.getFullyQualifiedName(), choice);

        JSONArray whenArray = new JSONArray();

        List<ChoiceConditionRoutePair> when = (List<ChoiceConditionRoutePair>) componentData.getData("when");
        for (ChoiceConditionRoutePair pair : when) {

            JSONObject conditionRoutePair = SerializerUtilities.newJSONObject();
            conditionRoutePair.put("condition", pair.getCondition());

            JSONArray nextArray = new JSONArray();

            GraphNode nextNode = pair.getNext();
            GraphSerializer.doSerialize(graph, nextArray, nextNode);

            conditionRoutePair.put("next", nextArray);

            whenArray.put(conditionRoutePair);
        }

        GraphNode otherwise = (GraphNode) componentData.getData("otherwise");

        choice.put("when", whenArray);
        return choice;
    }
}
