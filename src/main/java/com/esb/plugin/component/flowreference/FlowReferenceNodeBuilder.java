package com.esb.plugin.component.flowreference;

import com.esb.internal.commons.JsonParser;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.deserializer.AbstractBuilder;
import com.esb.plugin.graph.deserializer.BuilderContext;
import com.esb.plugin.graph.node.GraphNode;
import org.json.JSONObject;

public class FlowReferenceNodeBuilder extends AbstractBuilder {

    public FlowReferenceNodeBuilder(FlowGraph graph, BuilderContext context) {
        super(graph, context);
    }

    @Override
    public GraphNode build(GraphNode parent, JSONObject componentDefinition) {

        String name = JsonParser.Implementor.name(componentDefinition);

        FlowReferenceNode drawable = context.instantiateGraphNode(name);

        graph.add(parent, drawable);

        return drawable;
    }
}