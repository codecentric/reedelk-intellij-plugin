package com.esb.plugin.graph.action.strategy;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;

public class ReplaceRootStrategy implements Strategy {

    private final FlowGraph graph;

    public ReplaceRootStrategy(FlowGraph graph) {
        this.graph = graph;
    }

    @Override
    public void execute(GraphNode node) {
        // Only inbound components can replace root.
        if (node.isInbound()) {
            GraphNode currentRoot = graph.root();
            graph.root(node);
            graph.add(node, currentRoot);
        }
    }
}
