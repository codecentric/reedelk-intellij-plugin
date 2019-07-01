package com.esb.plugin.graph.action.add.strategy;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.action.Strategy;
import com.esb.plugin.graph.node.GraphNode;

/**
 * In a Subflow we don't replace the root node. We just add a node before.
 */
public class SubFlowAddNewRoot implements Strategy {

    private final FlowGraph graph;

    SubFlowAddNewRoot(FlowGraph graph) {
        this.graph = graph;
    }

    @Override
    public void execute(GraphNode node) {
        GraphNode currentRoot = graph.root();
        graph.root(node);
        graph.add(node, currentRoot);
    }
}
