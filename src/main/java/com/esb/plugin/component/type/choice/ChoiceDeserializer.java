package com.esb.plugin.component.type.choice;

import com.esb.plugin.component.domain.ComponentData;
import com.esb.plugin.component.type.stop.StopNode;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.deserializer.AbstractDeserializer;
import com.esb.plugin.graph.deserializer.DeserializerContext;
import com.esb.plugin.graph.deserializer.GraphDeserializerFactory;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.utils.CollectNodesBetween;
import com.esb.system.component.Stop;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.esb.internal.commons.JsonParser.Choice;
import static com.esb.internal.commons.JsonParser.Implementor;
import static com.esb.plugin.component.type.choice.ChoiceNode.DATA_CONDITION_ROUTE_PAIRS;

public class ChoiceDeserializer extends AbstractDeserializer {

    public ChoiceDeserializer(FlowGraph graph, DeserializerContext context) {
        super(graph, context);
    }

    @Override
    public GraphNode deserialize(GraphNode parent, JSONObject componentDefinition) {

        StopNode stopNode = context.instantiateGraphNode(Stop.class.getName());

        String name = Implementor.name(componentDefinition);

        ChoiceNode choiceNode = context.instantiateGraphNode(name);

        Map<GraphNode, String> preNodeConditionMap = new LinkedHashMap<>();

        graph.add(parent, choiceNode);

        // When
        JSONArray when = Choice.when(componentDefinition);

        for (int i = 0; i < when.length(); i++) {

            JSONObject whenComponent = when.getJSONObject(i);

            JSONArray next = Choice.next(whenComponent);

            GraphNode currentNode = deserialize(next, choiceNode, node -> {
                String condition = Choice.condition(whenComponent);
                preNodeConditionMap.put(node, condition);
            });

            // Last node is connected to stop node.
            graph.add(currentNode, stopNode);
        }

        // Add all the nodes just added (between choice and stop node) to the Choice's scope.
        CollectNodesBetween
                .them(graph, choiceNode, stopNode)
                .forEach(choiceNode::addToScope);

        ComponentData choiceData = choiceNode.componentData();

        // TODO: Not sure about this
        if (componentDefinition.has(Implementor.description())) {
            choiceData.set(Implementor.description(), Implementor.description(componentDefinition));
        }

        List<ChoiceConditionRoutePair> choiceConditionRoutePairList =
                choiceData.get(DATA_CONDITION_ROUTE_PAIRS);

        for (Map.Entry<GraphNode, String> entry : preNodeConditionMap.entrySet()) {
            // TODO: this is duplicated code
            choiceConditionRoutePairList.stream()
                    .filter(choiceConditionRoutePair -> choiceConditionRoutePair.getNext() == entry.getKey())
                    .findFirst()
                    .ifPresent(choiceConditionRoutePair -> choiceConditionRoutePair.setCondition(entry.getValue()));
        }

        return stopNode;
    }

    private interface Action {
        void perform(GraphNode node);
    }

    private GraphNode deserialize(JSONArray arrayToDeserialize, GraphNode parent, Action actionOnFirst) {
        GraphNode currentNode = parent;
        for (int i = 0; i < arrayToDeserialize.length(); i++) {
            JSONObject currentComponentDef = arrayToDeserialize.getJSONObject(i);
            currentNode = GraphDeserializerFactory.get()
                    .componentDefinition(currentComponentDef)
                    .context(context)
                    .graph(graph)
                    .build()
                    .deserialize(currentNode, currentComponentDef);

            if (i == 0) {
                actionOnFirst.perform(currentNode);
            }
        }
        return currentNode;
    }

}
