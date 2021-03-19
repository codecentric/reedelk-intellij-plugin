package de.codecentric.reedelk.plugin.editor.designer.action.add.strategy;

import de.codecentric.reedelk.plugin.graph.node.GraphNode;
import de.codecentric.reedelk.plugin.editor.designer.action.ActionStrategy;

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
