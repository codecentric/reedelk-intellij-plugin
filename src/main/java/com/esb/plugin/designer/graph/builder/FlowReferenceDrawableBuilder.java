package com.esb.plugin.designer.graph.builder;

import com.esb.internal.commons.JsonParser;
import com.esb.plugin.designer.editor.component.ComponentDescriptor;
import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.drawable.Drawable;
import com.esb.plugin.designer.graph.drawable.FlowReferenceDrawable;
import org.json.JSONObject;

public class FlowReferenceDrawableBuilder extends AbstractBuilder {

    FlowReferenceDrawableBuilder(FlowGraph graph, BuilderContext context) {
        super(graph, context);
    }

    @Override
    public Drawable build(Drawable parent, JSONObject componentDefinition) {

        String name = JsonParser.Implementor.name(componentDefinition);

        ComponentDescriptor component = context.instantiateComponent(name);

        FlowReferenceDrawable drawable = new FlowReferenceDrawable(component);

        graph.add(parent, drawable);

        return drawable;
    }
}
