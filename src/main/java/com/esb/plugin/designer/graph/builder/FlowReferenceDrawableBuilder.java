package com.esb.plugin.designer.graph.builder;

import com.esb.internal.commons.JsonParser;
import com.esb.plugin.designer.editor.component.Component;
import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.drawable.Drawable;
import com.esb.plugin.designer.graph.drawable.FlowReferenceDrawable;
import org.json.JSONObject;

public class FlowReferenceDrawableBuilder implements Builder {

    @Override
    public Drawable build(Drawable parent, JSONObject componentDefinition, FlowGraph graph) {

        String name = JsonParser.Implementor.name(componentDefinition);

        Component component = new Component(name);

        FlowReferenceDrawable flowReferenceDrawable = new FlowReferenceDrawable(component);

        graph.add(parent, flowReferenceDrawable);

        return flowReferenceDrawable;
    }
}
