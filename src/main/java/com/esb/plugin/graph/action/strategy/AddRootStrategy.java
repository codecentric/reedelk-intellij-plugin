package com.esb.plugin.graph.action.strategy;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;

public class AddRootStrategy implements Strategy {

    private final FlowGraph graph;

    public AddRootStrategy(FlowGraph graph) {
        this.graph = graph;
    }

    @Override
    public void execute(GraphNode node) {
        // Only inbound components can be added as root.
        if (node.isInbound()) {
            graph.root(node);
        }
    }
}
