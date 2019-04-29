package com.esb.plugin.designer.graph.deserializer;

import com.esb.internal.commons.JsonParser;
import com.esb.plugin.designer.editor.component.Component;
import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.GraphNode;
import com.esb.plugin.designer.graph.drawable.GenericComponentDrawable;
import org.json.JSONObject;

public class GenericDrawableBuilder extends AbstractBuilder {

    GenericDrawableBuilder(FlowGraph graph, BuilderContext context) {
        super(graph, context);
    }

    @Override
    public GraphNode build(GraphNode parent, JSONObject componentDefinition) {

        String name = JsonParser.Implementor.name(componentDefinition);

        Component component = context.instantiateComponent(name);

        // fill up data from component definition
        component.componentDataKeys()
                .forEach(propertyName -> {
                    Object propertyValue = componentDefinition.get(propertyName.toLowerCase());
                    component.setPropertyValue(propertyName, propertyValue);
                });

        GenericComponentDrawable drawable = new GenericComponentDrawable(component);

        graph.add(parent, drawable);

        return drawable;
    }

}
