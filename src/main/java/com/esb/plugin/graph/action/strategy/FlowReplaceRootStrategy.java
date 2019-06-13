package com.esb.plugin.graph.action.strategy;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;

import static com.esb.plugin.component.domain.ComponentClass.INBOUND;

public class FlowReplaceRootStrategy implements Strategy {

    private final FlowGraph graph;

    public FlowReplaceRootStrategy(FlowGraph graph) {
        this.graph = graph;
    }

    @Override
    public void execute(GraphNode node) {
        // Only inbound components can replace root.
        if (INBOUND.equals(node.getComponentClass())) {
            GraphNode currentRoot = graph.root();
            graph.root(node);
            graph.add(node, currentRoot);
        }
    }
}
