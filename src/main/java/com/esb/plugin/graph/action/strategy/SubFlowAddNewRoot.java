package com.esb.plugin.graph.action.strategy;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;

import static com.esb.plugin.component.domain.ComponentClass.INBOUND;

/**
 * In a Subflow we don't replace the root node. We just add a node before.
 */
public class SubFlowAddNewRoot implements Strategy {

    private final FlowGraph graph;

    public SubFlowAddNewRoot(FlowGraph graph) {
        this.graph = graph;
    }

    @Override
    public void execute(GraphNode node) {
        // For a subflow only components which are not inbound
        // can replace root.
        if (!INBOUND.equals(node.getComponentClass())) {
            GraphNode currentRoot = graph.root();
            graph.root(node);
            graph.add(node, currentRoot);
        }
    }
}
