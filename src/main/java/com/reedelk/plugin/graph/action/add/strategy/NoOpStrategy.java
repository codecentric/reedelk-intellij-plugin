package com.reedelk.plugin.graph.action.add.strategy;

import com.reedelk.plugin.graph.action.Strategy;
import com.reedelk.plugin.graph.node.GraphNode;

public class NoOpStrategy implements Strategy {

    @Override
    public void execute(GraphNode node) {
        // No op
    }

    @Override
    public boolean applicableOn(GraphNode node) {
        // This strategy can be applied on any node type.
        return true;
    }
}
