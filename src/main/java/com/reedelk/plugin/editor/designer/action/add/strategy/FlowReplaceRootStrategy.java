package com.reedelk.plugin.editor.designer.action.add.strategy;

import com.reedelk.plugin.editor.designer.action.remove.strategy.PlaceholderProvider;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.node.GraphNode;

import static com.reedelk.plugin.component.domain.ComponentClass.INBOUND;

public class FlowReplaceRootStrategy extends ReplaceNodeStrategy {

    FlowReplaceRootStrategy(FlowGraph graph, PlaceholderProvider placeholderProvider) {
        super(graph, graph.root(), placeholderProvider);
    }

    @Override
    public boolean applicableOn(GraphNode replacement) {
        // Only inbound components can replace root.
        return INBOUND.equals(replacement.getComponentClass());
    }
}
