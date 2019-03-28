package com.esb.plugin.designer.graph.builder;

import com.esb.internal.commons.JsonParser;
import com.esb.plugin.designer.editor.component.Component;
import com.esb.plugin.designer.graph.Drawable;
import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.drawable.GenericComponentDrawable;
import org.json.JSONObject;

public class GenericDrawableBuilder implements Builder<GenericComponentDrawable> {

    @Override
    public GenericComponentDrawable build(Drawable parent, JSONObject componentDefinition, FlowGraph graph) {

        String name = JsonParser.Implementor.name(componentDefinition);

        Component component = new Component(name);

        GenericComponentDrawable genericComponentDrawable = new GenericComponentDrawable(component);

        graph.add(parent, genericComponentDrawable);

        return genericComponentDrawable;
    }
}
