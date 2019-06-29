package com.esb.plugin.graph.action.strategy;

import com.esb.plugin.graph.node.GraphNode;

public class NoOpStrategy implements Strategy {

    @Override
    public void execute(GraphNode node) {
    }

    @Override
    public boolean applicableOn(GraphNode node) {
        // This strategy can be applied on any node type.
        return true;
    }
}
