package com.esb.plugin.component.stop;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.deserializer.AbstractBuilder;
import com.esb.plugin.graph.deserializer.BuilderContext;
import com.esb.plugin.graph.node.GraphNode;
import org.json.JSONObject;

public class StopNodeBuilder extends AbstractBuilder {

    public StopNodeBuilder(FlowGraph graph, BuilderContext context) {
        super(graph, context);
    }

    @Override
    public GraphNode build(GraphNode parent, JSONObject componentDefinition) {
        throw new UnsupportedOperationException();
    }
}
