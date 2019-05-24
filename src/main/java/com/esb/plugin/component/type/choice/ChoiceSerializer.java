package com.esb.plugin.component.type.choice;

import com.esb.plugin.component.domain.ComponentData;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.ScopedGraphNode;
import com.esb.plugin.graph.serializer.AbstractScopedNodeSerializer;
import com.esb.plugin.graph.serializer.GraphSerializerFactory;
import com.esb.plugin.graph.serializer.JsonObjectFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import static com.esb.internal.commons.JsonParser.Choice;
import static com.esb.internal.commons.JsonParser.Implementor;

public class ChoiceSerializer extends AbstractScopedNodeSerializer {

    @Override
    protected JSONObject serializeScopedNode(FlowGraph graph, ScopedGraphNode choiceNode, GraphNode stop) {
        ComponentData componentData = choiceNode.componentData();

        JSONObject choiceObject = JsonObjectFactory.newJSONObject();

        Implementor.name(componentData.getFullyQualifiedName(), choiceObject);

        String description = (String) componentData.get(Implementor.description());
        Implementor.description(description, choiceObject);

        List<ChoiceConditionRoutePair> conditionRoutePairs =
                (List<ChoiceConditionRoutePair>) componentData.get(ChoiceNode.DATA_CONDITION_ROUTE_PAIRS);

        JSONArray whenArrayObject = new JSONArray();

        List<GraphNode> choiceSuccessors = graph.successors(choiceNode);

        // Invert, we should first get the successors of the choice and then find the matching PAIR
        for (GraphNode choiceSuccessor : choiceSuccessors) {

            ChoiceConditionRoutePair pair = findPairFor(choiceSuccessor, conditionRoutePairs);

            JSONObject conditionAndRouteObject = JsonObjectFactory.newJSONObject();

            String condition = pair.getCondition();

            Choice.condition(condition, conditionAndRouteObject);

            JSONArray nextArrayObject = new JSONArray();

            GraphNode nextNode = pair.getNext();

            GraphSerializerFactory.get()
                    .node(nextNode)
                    .build()
                    .serialize(graph, nextArrayObject, nextNode, stop);

            Choice.next(nextArrayObject, conditionAndRouteObject);

            whenArrayObject.put(conditionAndRouteObject);
        }

        Choice.when(whenArrayObject, choiceObject);

        return choiceObject;
    }

    private ChoiceConditionRoutePair findPairFor(GraphNode choiceSuccessor, List<ChoiceConditionRoutePair> conditionRoutePairs) {
        return conditionRoutePairs.stream()
                .filter(choiceConditionRoutePair ->
                        choiceConditionRoutePair.getNext() == choiceSuccessor)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Choice condition route pair must be present."));
    }

}
