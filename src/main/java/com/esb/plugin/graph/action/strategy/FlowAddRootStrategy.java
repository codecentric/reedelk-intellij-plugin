package com.esb.plugin.graph.action.strategy;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;

import static com.esb.plugin.component.domain.ComponentClass.INBOUND;

public class FlowAddRootStrategy implements Strategy {

    private final FlowGraph graph;

    public FlowAddRootStrategy(FlowGraph graph) {
        this.graph = graph;
    }

    @Override
    public void execute(GraphNode node) {
        // Only inbound components can be added
        // as root inside flows.
        if (INBOUND.equals(node.getComponentClass())) {
            graph.root(node);
        }
    }
}