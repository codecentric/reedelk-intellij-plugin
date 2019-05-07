package com.esb.plugin.component.choice;

import com.esb.plugin.component.ComponentData;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.serializer.AbstractSerializer;
import com.esb.plugin.graph.serializer.GraphSerializer;
import com.esb.plugin.graph.serializer.JSONObjectFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import static com.esb.internal.commons.JsonParser.Choice;
import static com.esb.internal.commons.JsonParser.Implementor;
import static com.esb.plugin.component.choice.ChoiceNode.DEFAULT_CONDITION_NAME;
import static java.util.stream.Collectors.toList;

public class ChoiceSerializer extends AbstractSerializer {

    @Override
    public JSONObject serialize(FlowGraph graph, GraphNode node, GraphNode stop) {

        ComponentData componentData = node.component();

        JSONObject choiceObject = JSONObjectFactory.newJSONObject();

        Implementor.name(componentData.getFullyQualifiedName(), choiceObject);

        List<ChoiceConditionRoutePair> choiceConditionRoutePairList = (List<ChoiceConditionRoutePair>) componentData.get(ChoiceNode.DATA_CONDITION_ROUTE_PAIRS);

        List<ChoiceConditionRoutePair> when = choiceConditionRoutePairList.stream()
                .filter(choiceConditionRoutePair -> !choiceConditionRoutePair.getCondition().equals(DEFAULT_CONDITION_NAME))
                .collect(toList());

        JSONArray whenArrayObject = new JSONArray();

        for (ChoiceConditionRoutePair pair : when) {

            JSONObject conditionAndRouteObject = JSONObjectFactory.newJSONObject();

            String condition = pair.getCondition();

            Choice.condition(condition, conditionAndRouteObject);

            JSONArray nextArrayObject = new JSONArray();

            GraphNode nextNode = pair.getNext();

            GraphSerializer.doSerialize(graph, nextArrayObject, nextNode, stop);

            Choice.next(nextArrayObject, conditionAndRouteObject);

            whenArrayObject.put(conditionAndRouteObject);
        }

        Choice.when(whenArrayObject, choiceObject);

        GraphNode otherwiseNode = choiceConditionRoutePairList.stream()
                .filter(choiceConditionRoutePair -> choiceConditionRoutePair.getCondition().equals(DEFAULT_CONDITION_NAME))
                .findFirst()
                .get()
                .getNext();

        JSONArray otherwiseArray = new JSONArray();

        GraphSerializer.doSerialize(graph, otherwiseArray, otherwiseNode, stop);

        choiceObject.put(DEFAULT_CONDITION_NAME, otherwiseArray);

        return choiceObject;
    }

}
