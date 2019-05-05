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

        JSONObject choiceObject = SerializerUtilities.newJSONObject();

        Implementor.name(componentData.getFullyQualifiedName(), choiceObject);

        List<ChoiceConditionRoutePair> when = (List<ChoiceConditionRoutePair>) componentData.get("when");

        JSONArray whenArrayObject = new JSONArray();

        for (ChoiceConditionRoutePair pair : when) {

            JSONObject conditionAndRouteObject = SerializerUtilities.newJSONObject();

            String condition = pair.getCondition();

            conditionAndRouteObject.put("condition", condition);

            JSONArray nextArrayObject = new JSONArray();

            GraphNode nextNode = pair.getNext();

            GraphSerializer.doSerialize(graph, nextArrayObject, nextNode);

            conditionAndRouteObject.put("next", nextArrayObject);

            whenArrayObject.put(conditionAndRouteObject);
        }

        choiceObject.put("when", whenArrayObject);

        GraphNode otherwiseNode = (GraphNode) componentData.get("otherwise");

        JSONArray otherwiseArray = new JSONArray();

        GraphSerializer.doSerialize(graph, otherwiseArray, otherwiseNode);

        choiceObject.put("otherwise", otherwiseArray);

        return choiceObject;
    }
}
