package com.esb.plugin.component.choice;

import com.esb.plugin.component.ComponentData;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.serializer.AbstractSerializer;
import com.esb.plugin.graph.serializer.GraphSerializer;
import com.esb.plugin.graph.serializer.JsonObjectFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import static com.esb.internal.commons.JsonParser.Choice;
import static com.esb.internal.commons.JsonParser.Implementor;
import static com.esb.plugin.component.choice.ChoiceNode.DEFAULT_CONDITION_NAME;
import static java.util.stream.Collectors.toList;

public class ChoiceSerializer extends AbstractSerializer {

    @Override
    public JSONObject serialize(FlowGraph graph, GraphNode choiceNode, GraphNode stop) {

        ComponentData componentData = choiceNode.componentData();

        JSONObject choiceObject = JsonObjectFactory.newJSONObject();

        Implementor.name(componentData.getFullyQualifiedName(), choiceObject);

        List<ChoiceConditionRoutePair> choiceConditionRoutePairList = (List<ChoiceConditionRoutePair>) componentData.get(ChoiceNode.DATA_CONDITION_ROUTE_PAIRS);

        List<ChoiceConditionRoutePair> when = choiceConditionRoutePairList.stream()
                .filter(choiceConditionRoutePair -> !choiceConditionRoutePair.getCondition().equals(DEFAULT_CONDITION_NAME))
                .collect(toList());

        JSONArray whenArrayObject = new JSONArray();

        // Invert, we should first get the successors of the choice and then find the matching PAIR
        for (ChoiceConditionRoutePair pair : when) {

            JSONObject conditionAndRouteObject = JsonObjectFactory.newJSONObject();

            String condition = pair.getCondition();

            Choice.condition(condition, conditionAndRouteObject);

            JSONArray nextArrayObject = new JSONArray();

            GraphNode nextNode = pair.getNext();

            GraphSerializer.doSerialize(graph, nextArrayObject, nextNode, stop);

            Choice.next(nextArrayObject, conditionAndRouteObject);

            whenArrayObject.put(conditionAndRouteObject);
        }

        Choice.when(whenArrayObject, choiceObject);

        // TODO: This has to be checked, understand what to do if get() does not exists!
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
