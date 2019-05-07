package com.esb.plugin.component.choice;

import com.esb.component.Stop;
import com.esb.plugin.component.ComponentData;
import com.esb.plugin.component.stop.StopNode;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.deserializer.AbstractDeserializer;
import com.esb.plugin.graph.deserializer.DeserializerContext;
import com.esb.plugin.graph.deserializer.GraphDeserializerFactory;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.utils.CollectNodesBetween;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.esb.internal.commons.JsonParser.Choice;
import static com.esb.internal.commons.JsonParser.Implementor;

public class ChoiceDeserializer extends AbstractDeserializer {

    public ChoiceDeserializer(FlowGraph graph, DeserializerContext context) {
        super(graph, context);
    }

    @Override
    public GraphNode deserialize(GraphNode parent, JSONObject componentDefinition) {

        StopNode stopNode = context.instantiateGraphNode(Stop.class.getName());

        String name = Implementor.name(componentDefinition);

        ChoiceNode choiceNode = context.instantiateGraphNode(name);

        Map<GraphNode, String> preNodeConditionMap = new HashMap<>();

        graph.add(parent, choiceNode);

        // When
        JSONArray when = Choice.getWhen(componentDefinition);

        for (int i = 0; i < when.length(); i++) {

            JSONObject whenComponent = when.getJSONObject(i);

            JSONArray next = Choice.getNext(whenComponent);

            GraphNode currentNode = deserialize(next, choiceNode, node -> {
                String condition = Choice.getCondition(whenComponent);
                preNodeConditionMap.put(node, condition);
            });

            // Last node is connected to stop node.
            graph.add(currentNode, stopNode);
        }

        // Otherwise
        JSONArray otherwise = Choice.getOtherwise(componentDefinition);

        GraphNode currentNode = deserialize(otherwise, choiceNode, node ->
                preNodeConditionMap.put(node, "otherwise"));

        // Last node is connected to stop node.
        graph.add(currentNode, stopNode);

        CollectNodesBetween.them(graph, choiceNode, stopNode)
                .forEach(choiceNode::addToScope);

        ComponentData choiceData = choiceNode.component();
        Map<GraphNode, String> nodeConditionMap = (Map<GraphNode, String>) choiceData.get("nodeConditionMap");

        for (Map.Entry<GraphNode, String> entry : preNodeConditionMap.entrySet()) {
            GraphNode node = entry.getKey();
            String condition = entry.getValue();
            nodeConditionMap.replace(node, condition);
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
