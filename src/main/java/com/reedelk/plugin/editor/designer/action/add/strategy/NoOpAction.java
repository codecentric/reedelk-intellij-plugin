package com.reedelk.plugin.editor.designer.action.add.strategy;

import com.reedelk.plugin.editor.designer.action.ActionStrategy;
import com.reedelk.plugin.graph.node.GraphNode;

public class NoOpAction implements ActionStrategy {

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
