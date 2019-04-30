package com.esb.plugin.component.choice;

import com.esb.internal.commons.JsonParser;
import com.esb.plugin.component.Component;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.GraphNode;
import com.esb.plugin.graph.deserializer.AbstractBuilder;
import com.esb.plugin.graph.deserializer.BuilderContext;
import com.esb.plugin.graph.deserializer.DrawableBuilder;
import com.esb.plugin.graph.drawable.StopGraphNode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChoiceGraphNodeBuilder extends AbstractBuilder {

    public ChoiceGraphNodeBuilder(FlowGraph graph, BuilderContext context) {
        super(graph, context);
    }

    @Override
    public GraphNode build(GraphNode parent, JSONObject componentDefinition) {

        StopGraphNode stopDrawable = new StopGraphNode();

        String name = JsonParser.Implementor.name(componentDefinition);

        Component component = context.instantiateComponent(name);

        ChoiceGraphNode choiceDrawable = new ChoiceGraphNode(component);

        graph.add(parent, choiceDrawable);

        // When
        JSONArray when = JsonParser.Choice.getWhen(componentDefinition);
        for (int i = 0; i < when.length(); i++) {
            JSONObject whenComponent = when.getJSONObject(i);

            GraphNode currentDrawable = choiceDrawable;

            JSONArray next = JsonParser.Choice.getNext(whenComponent);
            currentDrawable = buildArrayOfComponents(graph, currentDrawable, next);

            // Last node is connected to stop node.
            graph.add(currentDrawable, stopDrawable);
        }

        // Otherwise
        GraphNode currentDrawable = choiceDrawable;

        JSONArray otherwise = JsonParser.Choice.getOtherwise(componentDefinition);
        currentDrawable = buildArrayOfComponents(graph, currentDrawable, otherwise);

        // Last node is stop node.
        graph.add(currentDrawable, stopDrawable);

        collectNodesBetween(graph, choiceDrawable, stopDrawable)
                .forEach(choiceDrawable::addToScope);

        return stopDrawable;
    }

    private GraphNode buildArrayOfComponents(FlowGraph graph, GraphNode currentDrawable, JSONArray next) {
        for (int j = 0; j < next.length(); j++) {
            JSONObject currentComponentDef = next.getJSONObject(j);
            currentDrawable = DrawableBuilder.get()
                    .componentDefinition(currentComponentDef)
                    .graph(graph)
                    .context(context)
                    .parent(currentDrawable)
                    .build();
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
