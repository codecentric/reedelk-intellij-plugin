package com.reedelk.plugin.editor.designer.action.add.strategy;

import com.reedelk.module.descriptor.model.component.ComponentType;
import com.reedelk.plugin.editor.designer.action.remove.strategy.PlaceholderProvider;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.node.GraphNode;

public class FlowReplaceRootAction extends ReplaceNodeAction {

    FlowReplaceRootAction(FlowGraph graph, PlaceholderProvider placeholderProvider) {
        super(graph, graph.root(), placeholderProvider);
    }

    @Override
    public boolean applicableOn(GraphNode replacement) {
        // Only inbound components can replace root.
        return ComponentType.INBOUND.equals(replacement.getComponentClass());
    }
}
