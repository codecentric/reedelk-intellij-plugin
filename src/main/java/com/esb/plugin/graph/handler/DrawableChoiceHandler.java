package com.esb.plugin.graph.handler;

import com.esb.internal.commons.JsonParser;
import com.esb.plugin.designer.editor.component.Component;
import com.esb.plugin.graph.FlowGraph;
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

            Drawable currentNode = drawableChoice;

            JSONArray next = JsonParser.Choice.getNext(whenComponent);
            for (int j = 0; j < next.length(); j++) {
                JSONObject currentComponentDef = next.getJSONObject(j);
                currentNode = HandlerFactory.get(currentComponentDef).handle(currentNode, currentComponentDef, graph);
            }

            graph.add(currentNode, stopDrawable);
        }


        // Otherwise
        Drawable currentNode = drawableChoice;

        JSONArray otherwise = JsonParser.Choice.getOtherwise(implementorDefinition);
        for (int i = 0; i < otherwise.length(); i++) {
            JSONObject currentComponentDef = otherwise.getJSONObject(i);
            Drawable lastNode = HandlerFactory.get(currentComponentDef).handle(currentNode, currentComponentDef, graph);

            graph.add(currentNode, lastNode);
            currentNode = lastNode;
        }


        // Last node is stop node.
        graph.add(currentNode, stopDrawable);
        return stopDrawable;
    }
}
