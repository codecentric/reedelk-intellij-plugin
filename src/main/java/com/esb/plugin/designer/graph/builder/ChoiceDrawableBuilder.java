package com.esb.plugin.designer.graph.builder;

import com.esb.internal.commons.JsonParser;
import com.esb.plugin.designer.editor.component.Component;
import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.drawable.ChoiceDrawable;
import com.esb.plugin.designer.graph.drawable.Drawable;
import com.esb.plugin.designer.graph.drawable.StopDrawable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChoiceDrawableBuilder implements Builder {

    @Override
    public Drawable build(Drawable parent, JSONObject componentDefinition, FlowGraph graph) {

        StopDrawable stopDrawable = new StopDrawable();

        String name = JsonParser.Implementor.name(componentDefinition);

        Component component = new Component(name);

        ChoiceDrawable choiceDrawable = new ChoiceDrawable(component);

        graph.add(parent, choiceDrawable);

        // When
        JSONArray when = JsonParser.Choice.getWhen(componentDefinition);
        for (int i = 0; i < when.length(); i++) {
            JSONObject whenComponent = when.getJSONObject(i);

            Drawable currentDrawable = choiceDrawable;

            JSONArray next = JsonParser.Choice.getNext(whenComponent);
            currentDrawable = buildArrayOfComponents(graph, currentDrawable, next);

            // Last node is connected to stop node.
            graph.add(currentDrawable, stopDrawable);
        }

        // Otherwise
        Drawable currentDrawable = choiceDrawable;

        JSONArray otherwise = JsonParser.Choice.getOtherwise(componentDefinition);
        currentDrawable = buildArrayOfComponents(graph, currentDrawable, otherwise);

        // Last node is stop node.
        graph.add(currentDrawable, stopDrawable);

        Collection<Drawable> allNodesBelongingToThisChoice = listNodesBetween(graph, choiceDrawable, stopDrawable);
        allNodesBelongingToThisChoice
                .forEach(choiceDrawable::addInScope);

        return stopDrawable;
    }

    private Drawable buildArrayOfComponents(FlowGraph graph, Drawable currentDrawable, JSONArray next) {
        for (int j = 0; j < next.length(); j++) {
            JSONObject currentComponentDef = next.getJSONObject(j);
            currentDrawable = BuilderFactory.get(currentComponentDef).build(currentDrawable, currentComponentDef, graph);
        }
        return currentDrawable;
    }

    private Collection<Drawable> listNodesBetween(FlowGraph graph, Drawable n1, Drawable n2) {
        Set<Drawable> accumulator = new HashSet<>();
        List<Drawable> successors = graph.successors(n1);
        for (Drawable successor : successors) {
            if (successor != n2) {
                accumulator.add(successor);
            }
            accumulator.addAll(listNodesBetween(graph, successor, n2));
        }
        return accumulator;
    }
}
