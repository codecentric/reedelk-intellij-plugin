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

    @Override
    public JSONObject serialize(FlowGraph graph, GraphNode node) {
        ComponentData componentData = node.component();

        JSONObject choice = SerializerUtilities.newJSONObject();

        Implementor.name(componentData.getFullyQualifiedName(), choice);

        JSONArray whenArray = new JSONArray();

        List<ChoiceConditionRoutePair> when = (List<ChoiceConditionRoutePair>) componentData.get("when");

        for (ChoiceConditionRoutePair pair : when) {

            JSONObject conditionRoutePair = SerializerUtilities.newJSONObject();

            conditionRoutePair.put("condition", pair.getCondition());

            JSONArray nextArray = new JSONArray();

            GraphNode nextNode = pair.getNext();

            GraphSerializer.doSerialize(graph, nextArray, nextNode);

            conditionRoutePair.put("next", nextArray);

            whenArray.put(conditionRoutePair);
        }

        choice.put("when", whenArray);

        GraphNode otherwiseNode = (GraphNode) componentData.get("otherwise");

        JSONArray otherwiseArray = new JSONArray();

        GraphSerializer.doSerialize(graph, otherwiseArray, otherwiseNode);

        choice.put("otherwise", otherwiseArray);

        return choice;
    }
}
