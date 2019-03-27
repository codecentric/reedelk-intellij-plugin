package com.esb.plugin.graph.handler;

import com.esb.internal.commons.JsonParser;
import com.esb.plugin.designer.editor.component.Component;
import com.esb.plugin.graph.FlowGraph;
import org.json.JSONObject;

public class DrawableGenericHandler implements DrawableComponentHandler<DrawableGeneric> {

    @Override
    public DrawableGeneric handle(Drawable parent, JSONObject implementorDefinition, FlowGraph graph) {

        String name = JsonParser.Implementor.name(implementorDefinition);

        Component component = new Component(name);

        DrawableGeneric drawableGeneric = new DrawableGeneric(component);

        graph.add(parent, drawableGeneric);

        return drawableGeneric;
    }
}
