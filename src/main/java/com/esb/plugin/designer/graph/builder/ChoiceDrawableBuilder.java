package com.esb.plugin.designer.graph.builder;

import com.esb.internal.commons.JsonParser;
import com.esb.plugin.designer.editor.component.Component;
import com.esb.plugin.designer.graph.Drawable;
import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.drawable.ChoiceDrawable;
import com.esb.plugin.designer.graph.drawable.StopDrawable;
import org.json.JSONArray;
import org.json.JSONObject;

public class ChoiceDrawableBuilder implements Builder<StopDrawable> {

    @Override
    public StopDrawable build(Drawable parent, JSONObject componentDefinition, FlowGraph graph) {

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
            for (int j = 0; j < next.length(); j++) {
                JSONObject currentComponentDef = next.getJSONObject(j);
                currentDrawable = BuilderFactory.get(currentComponentDef).build(currentDrawable, currentComponentDef, graph);
            }

            graph.add(currentDrawable, stopDrawable);
        }


        // Otherwise
        Drawable currentDrawable = choiceDrawable;

        JSONArray otherwise = JsonParser.Choice.getOtherwise(componentDefinition);
        for (int i = 0; i < otherwise.length(); i++) {
            JSONObject currentComponentDef = otherwise.getJSONObject(i);
            currentDrawable = BuilderFactory.get(currentComponentDef).build(currentDrawable, currentComponentDef, graph);
        }


        // Last node is stop node.
        graph.add(currentDrawable, stopDrawable);
        return stopDrawable;
    }
}
