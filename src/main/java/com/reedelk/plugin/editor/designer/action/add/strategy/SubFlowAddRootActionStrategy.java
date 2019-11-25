package com.reedelk.plugin.editor.designer.action.add.strategy;

import com.reedelk.plugin.editor.designer.action.ActionStrategy;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.node.GraphNode;

/**
 * A Subflow can only start with components which don't have
 * INBOUND class type.
 */
public class SubFlowAddRootActionStrategy implements ActionStrategy {

    private final FlowGraph graph;

    SubFlowAddRootActionStrategy(FlowGraph graph) {
        this.graph = graph;
    }

    @Override
    public void execute(GraphNode node) {
        graph.root(node);
    }

}
