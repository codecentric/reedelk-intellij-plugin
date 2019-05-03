package com.esb.plugin.component.generic;

import com.esb.internal.commons.JsonParser;
import com.esb.plugin.component.Component;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.deserializer.AbstractBuilder;
import com.esb.plugin.graph.deserializer.BuilderContext;
import com.esb.plugin.graph.node.GraphNode;
import org.json.JSONObject;

public class GenericComponentNodeBuilder extends AbstractBuilder {

    public GenericComponentNodeBuilder(FlowGraph graph, BuilderContext context) {
        super(graph, context);
    }

    @Override
    public GraphNode build(GraphNode parent, JSONObject componentDefinition) {

        String name = JsonParser.Implementor.name(componentDefinition);

        GenericComponentNode node = context.instantiateGraphNode(name);

        Component component = node.component();

        // fill up data from component definition
        component.componentDataKeys()
                .forEach(propertyName -> {
                    Object propertyValue = componentDefinition.get(propertyName.toLowerCase());
                    // Explicitly map JSON Library NULL value to java's null.
                    if (propertyValue == JSONObject.NULL) {
                        propertyValue = null;
                    }
                    component.setPropertyValue(propertyName, propertyValue);
                });

        graph.add(parent, node);

        return node;
    }

}
