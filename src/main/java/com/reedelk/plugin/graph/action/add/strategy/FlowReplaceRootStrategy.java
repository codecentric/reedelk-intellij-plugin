package com.reedelk.plugin.graph.action.add.strategy;

import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.node.GraphNode;

import static com.reedelk.plugin.component.domain.ComponentClass.INBOUND;

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
