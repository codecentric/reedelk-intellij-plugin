package com.esb.plugin.graph.action.add.strategy;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.action.Strategy;
import com.esb.plugin.graph.node.GraphNode;

/**
 * A Subflow can only start with components which don't have
 * INBOUND class type.
 */
public class SubFlowAddRootStrategy implements Strategy {

    private final FlowGraph graph;

    SubFlowAddRootStrategy(FlowGraph graph) {
        this.graph = graph;
    }

    @Override
    public void execute(GraphNode node) {
        graph.root(node);
    }

}
