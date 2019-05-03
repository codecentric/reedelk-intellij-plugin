package com.esb.plugin.component.choice;

import com.esb.component.Stop;
import com.esb.plugin.component.stop.StopNode;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.deserializer.AbstractDeserializer;
import com.esb.plugin.graph.deserializer.DeserializerContext;
import com.esb.plugin.graph.deserializer.GraphDeserializerFactory;
import com.esb.plugin.graph.node.GraphNode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

        graph.add(parent, choiceNode);

        // When
        JSONArray when = Choice.getWhen(componentDefinition);
        for (int i = 0; i < when.length(); i++) {
            JSONObject whenComponent = when.getJSONObject(i);

            GraphNode currentDrawable = choiceNode;

            JSONArray next = Choice.getNext(whenComponent);
            currentDrawable = buildArrayOfComponents(graph, currentDrawable, next);

            // Last node is connected to stop node.
            graph.add(currentDrawable, stopNode);
        }

        // Otherwise
        GraphNode currentDrawable = choiceNode;

        JSONArray otherwise = Choice.getOtherwise(componentDefinition);
        currentDrawable = buildArrayOfComponents(graph, currentDrawable, otherwise);

        // Last node is stop node.
        graph.add(currentDrawable, stopNode);

        collectNodesBetween(graph, choiceNode, stopNode)
                .forEach(choiceNode::addToScope);

        return stopNode;
    }

    private GraphNode buildArrayOfComponents(FlowGraph graph, GraphNode currentDrawable, JSONArray next) {
        for (int j = 0; j < next.length(); j++) {
            JSONObject currentComponentDef = next.getJSONObject(j);
            currentDrawable = GraphDeserializerFactory.get()
                    .componentDefinition(currentComponentDef)
                    .context(context)
                    .graph(graph)
                    .build()
                    .deserialize(currentDrawable, currentComponentDef);
        }
        return currentDrawable;
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
