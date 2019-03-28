package com.esb.plugin.designer.graph.builder;

import com.esb.internal.commons.JsonParser;
import com.esb.plugin.designer.editor.component.Component;
import com.esb.plugin.designer.graph.Drawable;
import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.drawable.DrawableGeneric;
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
