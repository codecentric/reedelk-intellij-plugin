package com.esb.plugin.designer.graph.builder;

import com.esb.internal.commons.JsonParser;
import com.esb.plugin.designer.editor.component.Component;
import com.esb.plugin.designer.graph.Drawable;
import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.drawable.DrawableChoice;
import com.esb.plugin.designer.graph.drawable.StopDrawable;
import org.json.JSONArray;
import org.json.JSONObject;

public class DrawableChoiceHandler implements DrawableComponentHandler<StopDrawable> {

    @Override
    public StopDrawable handle(Drawable parent, JSONObject implementorDefinition, FlowGraph graph) {

        StopDrawable stopDrawable = new StopDrawable();

        String name = JsonParser.Implementor.name(implementorDefinition);

        Component component = new Component(name);

        DrawableChoice drawableChoice = new DrawableChoice(component);

        graph.add(parent, drawableChoice);

        // When
        JSONArray when = JsonParser.Choice.getWhen(implementorDefinition);
        for (int i = 0; i < when.length(); i++) {
            JSONObject whenComponent = when.getJSONObject(i);

            Drawable currentDrawable = drawableChoice;

            JSONArray next = JsonParser.Choice.getNext(whenComponent);
            for (int j = 0; j < next.length(); j++) {
                JSONObject currentComponentDef = next.getJSONObject(j);
                currentDrawable = HandlerFactory.get(currentComponentDef).handle(currentDrawable, currentComponentDef, graph);
            }

            graph.add(currentDrawable, stopDrawable);
        }


        // Otherwise
        Drawable currentDrawable = drawableChoice;

        JSONArray otherwise = JsonParser.Choice.getOtherwise(implementorDefinition);
        for (int i = 0; i < otherwise.length(); i++) {
            JSONObject currentComponentDef = otherwise.getJSONObject(i);
            currentDrawable = HandlerFactory.get(currentComponentDef).handle(currentDrawable, currentComponentDef, graph);
        }


        // Last node is stop node.
        graph.add(currentDrawable, stopDrawable);
        return stopDrawable;
    }
}
