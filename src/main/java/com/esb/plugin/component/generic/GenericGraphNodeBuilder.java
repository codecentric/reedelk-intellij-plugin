package com.esb.plugin.component.generic;

import com.esb.internal.commons.JsonParser;
import com.esb.plugin.component.Component;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.deserializer.AbstractBuilder;
import com.esb.plugin.graph.deserializer.BuilderContext;
import com.esb.plugin.graph.node.GraphNode;
import org.json.JSONObject;

public class GenericGraphNodeBuilder extends AbstractBuilder {

    public GenericGraphNodeBuilder(FlowGraph graph, BuilderContext context) {
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

        GenericComponentGraphNode drawable = new GenericComponentGraphNode(component);

        graph.add(parent, drawable);

        return drawable;
    }

}
