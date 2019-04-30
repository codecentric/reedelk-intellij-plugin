package com.esb.plugin.component.flowreference;

import com.esb.internal.commons.JsonParser;
import com.esb.plugin.component.Component;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.GraphNode;
import com.esb.plugin.graph.deserializer.AbstractBuilder;
import com.esb.plugin.graph.deserializer.BuilderContext;
import org.json.JSONObject;

public class FlowReferenceGraphNodeBuilder extends AbstractBuilder {

    public FlowReferenceGraphNodeBuilder(FlowGraph graph, BuilderContext context) {
        super(graph, context);
    }

    @Override
    public GraphNode build(GraphNode parent, JSONObject componentDefinition) {

        String name = JsonParser.Implementor.name(componentDefinition);

        Component component = context.instantiateComponent(name);

        FlowReferenceGraphNode drawable = new FlowReferenceGraphNode(component);

        graph.add(parent, drawable);

        return drawable;
    }
}
