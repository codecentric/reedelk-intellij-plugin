package com.esb.plugin.designer.graph.deserializer;

import com.esb.internal.commons.JsonParser;
import com.esb.plugin.designer.editor.component.Component;
import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.GraphNode;
import com.esb.plugin.designer.graph.drawable.ChoiceDrawable;
import com.esb.plugin.designer.graph.drawable.StopDrawable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChoiceDrawableBuilder extends AbstractBuilder {

    ChoiceDrawableBuilder(FlowGraph graph, BuilderContext context) {
        super(graph, context);
    }

    @Override
    public GraphNode build(GraphNode parent, JSONObject componentDefinition) {

        StopDrawable stopDrawable = new StopDrawable();

        String name = JsonParser.Implementor.name(componentDefinition);

        Component component = context.instantiateComponent(name);

        ChoiceDrawable choiceDrawable = new ChoiceDrawable(component);

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
