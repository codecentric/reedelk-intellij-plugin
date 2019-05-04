package com.esb.plugin.component.choice;

import com.esb.component.Stop;
import com.esb.plugin.component.ComponentData;
import com.esb.plugin.component.stop.StopNode;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.deserializer.AbstractDeserializer;
import com.esb.plugin.graph.deserializer.DeserializerContext;
import com.esb.plugin.graph.deserializer.GraphDeserializerFactory;
import com.esb.plugin.graph.node.GraphNode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

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

        ComponentData choiceNodeComponentData = choiceNode.component();
        List<ChoiceConditionRoutePair> conditionRouteList = new ArrayList<>();
        choiceNodeComponentData.setPropertyValue("when", conditionRouteList);

        graph.add(parent, choiceNode);

        // When
        JSONArray when = Choice.getWhen(componentDefinition);
        for (int i = 0; i < when.length(); i++) {
            JSONObject whenComponent = when.getJSONObject(i);

            GraphNode currentNode = choiceNode;

            JSONArray next = Choice.getNext(whenComponent);

            for (int j = 0; j < next.length(); j++) {

                JSONObject currentComponentDef = next.getJSONObject(j);
                currentNode = GraphDeserializerFactory.get()
                        .componentDefinition(currentComponentDef)
                        .context(context)
                        .graph(graph)
                        .build()
                        .deserialize(currentNode, currentComponentDef);

                // First node we must keep track of the condition -> node pair
                if (j == 0) {
                    String condition = Choice.getCondition(whenComponent);
                    conditionRouteList.add(new ChoiceConditionRoutePair(condition, currentNode));
                }
            }

            // Last node is connected to stop node.
            graph.add(currentNode, stopNode);
        }

        // Otherwise
        GraphNode currentDrawable = choiceNode;

        JSONArray otherwise = Choice.getOtherwise(componentDefinition);

        for (int j = 0; j < otherwise.length(); j++) {
            JSONObject currentComponentDef = otherwise.getJSONObject(j);
            currentDrawable = GraphDeserializerFactory.get()
                    .componentDefinition(currentComponentDef)
                    .context(context)
                    .graph(graph)
                    .build()
                    .deserialize(currentDrawable, currentComponentDef);

            // First node we must keep track of the otherwise -> node condition
            if (j == 0) {
                choiceNodeComponentData.setPropertyValue("otherwise", currentDrawable);
            }
        }

        // Last node is stop node.
        graph.add(currentDrawable, stopNode);

        collectNodesBetween(graph, choiceNode, stopNode)
                .forEach(choiceNode::addToScope);

        return stopNode;
    }

    private Collection<GraphNode> collectNodesBetween(FlowGraph graph, GraphNode n1, GraphNode n2) {
        Set<GraphNode> accumulator = new HashSet<>();
        List<GraphNode> successors = graph.successors(n1);
        for (GraphNode successor : successors) {
            if (successor != n2) {
                accumulator.add(successor);
            }
            accumulator.addAll(collectNodesBetween(graph, successor, n2));
        }
        return accumulator;
    }
}
