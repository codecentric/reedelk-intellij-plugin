package com.esb.plugin.graph.action.strategy;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;

import static com.esb.plugin.component.domain.ComponentClass.INBOUND;

public class FlowReplaceRootStrategy extends ReplaceNodeStrategy {

    FlowReplaceRootStrategy(FlowGraph graph) {
        super(graph, graph.root());
    }

    @Override
    public boolean applicableOn(GraphNode replacement) {
        // Only inbound components can replace root.
        return INBOUND.equals(replacement.getComponentClass());
    }
}
