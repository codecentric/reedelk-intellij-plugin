package com.esb.plugin.graph.action.strategy;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;

import static com.esb.plugin.component.domain.ComponentClass.INBOUND;

public class SubFlowAddRootStrategy implements Strategy {

    private final FlowGraph graph;

    public SubFlowAddRootStrategy(FlowGraph graph) {
        this.graph = graph;
    }

    @Override
    public void execute(GraphNode node) {
        // Subflows can start with any class type component except
        // INBOUND class type.
        if (!INBOUND.equals(node.getComponentClass())) {
            graph.root(node);
        }
    }
}
