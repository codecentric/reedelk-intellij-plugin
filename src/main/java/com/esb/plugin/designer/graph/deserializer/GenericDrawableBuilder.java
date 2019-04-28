package com.esb.plugin.designer.graph.deserializer;

import com.esb.internal.commons.JsonParser;
import com.esb.plugin.designer.editor.component.ComponentDescriptor;
import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.drawable.Drawable;
import com.esb.plugin.designer.graph.drawable.GenericComponentDrawable;
import org.json.JSONObject;

public class GenericDrawableBuilder extends AbstractBuilder {

    GenericDrawableBuilder(FlowGraph graph, BuilderContext context) {
        super(graph, context);
    }

    @Override
    public Drawable build(Drawable parent, JSONObject componentDefinition) {

        String name = JsonParser.Implementor.name(componentDefinition);

        ComponentDescriptor component = context.instantiateComponent(name);

        // fill up data from component definition
        component
                .componentDataKeys()
                .forEach(propertyName ->
                        component.setPropertyValue(propertyName, componentDefinition.get(propertyName)));

        GenericComponentDrawable drawable = new GenericComponentDrawable(component);

        graph.add(parent, drawable);

        return drawable;
    }

}
