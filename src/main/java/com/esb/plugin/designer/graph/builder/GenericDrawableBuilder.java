package com.esb.plugin.designer.graph.builder;

import com.esb.internal.commons.JsonParser;
import com.esb.plugin.designer.editor.component.ComponentDescriptor;
import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.drawable.Drawable;
import com.esb.plugin.designer.graph.drawable.GenericComponentDrawable;
import org.json.JSONObject;

public class GenericDrawableBuilder implements Builder {

    @Override
    public Drawable build(Drawable parent, JSONObject componentDefinition, FlowGraph graph) {

        String name = JsonParser.Implementor.name(componentDefinition);

        ComponentDescriptor component = new ComponentDescriptor(name);

        GenericComponentDrawable genericComponentDrawable = new GenericComponentDrawable(component);

        graph.add(parent, genericComponentDrawable);

        return genericComponentDrawable;
    }
}
