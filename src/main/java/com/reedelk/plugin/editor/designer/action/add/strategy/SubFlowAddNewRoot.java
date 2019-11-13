package com.reedelk.plugin.editor.designer.action.add.strategy;

import com.reedelk.plugin.editor.designer.action.Strategy;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.node.GraphNode;

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
