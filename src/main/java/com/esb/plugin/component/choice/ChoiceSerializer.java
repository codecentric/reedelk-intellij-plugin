package com.esb.plugin.component.choice;

import com.esb.plugin.component.ComponentData;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.serializer.AbstractSerializer;
import com.esb.plugin.graph.serializer.GraphSerializer;
import com.esb.plugin.graph.serializer.JSONObjectFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.esb.internal.commons.JsonParser.Implementor;
import static java.util.stream.Collectors.toList;

public class ChoiceSerializer extends AbstractSerializer {

    @Override
    public JSONObject serialize(FlowGraph graph, GraphNode node) {

        ComponentData componentData = node.component();

        JSONObject choiceObject = JSONObjectFactory.newJSONObject();

        Implementor.name(componentData.getFullyQualifiedName(), choiceObject);


        Map<GraphNode, String> nodeConditionMap = (Map<GraphNode, String>) componentData.get("nodeConditionMap");

        List<ChoiceConditionRoutePair> pairs = new ArrayList<>();

        nodeConditionMap.forEach((n, condition) -> pairs.add(new ChoiceConditionRoutePair(condition, n)));

        List<ChoiceConditionRoutePair> when = pairs.stream()
                .filter(choiceConditionRoutePair -> !choiceConditionRoutePair.getCondition().equals("otherwise"))
                .collect(toList());



        JSONArray whenArrayObject = new JSONArray();

        for (ChoiceConditionRoutePair pair : when) {

            JSONObject conditionAndRouteObject = JSONObjectFactory.newJSONObject();

            String condition = pair.getCondition();

            conditionAndRouteObject.put("condition", condition);

            JSONArray nextArrayObject = new JSONArray();

            GraphNode nextNode = pair.getNext();

            GraphSerializer.doSerialize(graph, nextArrayObject, nextNode);

            conditionAndRouteObject.put("next", nextArrayObject);

            whenArrayObject.put(conditionAndRouteObject);
        }

        choiceObject.put("when", whenArrayObject);

        GraphNode otherwiseNode = pairs.stream()
                .filter(choiceConditionRoutePair -> choiceConditionRoutePair.getCondition().equals("otherwise"))
                .findFirst().get().getNext();

        JSONArray otherwiseArray = new JSONArray();

        GraphSerializer.doSerialize(graph, otherwiseArray, otherwiseNode);

        choiceObject.put("otherwise", otherwiseArray);

        return choiceObject;
    }
}
