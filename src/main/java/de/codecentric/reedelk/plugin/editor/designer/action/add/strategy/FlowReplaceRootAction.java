package de.codecentric.reedelk.plugin.editor.designer.action.add.strategy;

import de.codecentric.reedelk.plugin.graph.FlowGraph;
import de.codecentric.reedelk.plugin.graph.node.GraphNode;
import de.codecentric.reedelk.module.descriptor.model.component.ComponentType;
import de.codecentric.reedelk.plugin.editor.designer.action.remove.strategy.PlaceholderProvider;

public class FlowReplaceRootAction extends ReplaceNodeAction {

    FlowReplaceRootAction(FlowGraph graph, PlaceholderProvider placeholderProvider) {
        super(graph, graph.root(), placeholderProvider);
    }

    @Override
    public boolean applicableOn(GraphNode replacement) {
        // Only inbound components can replace root.
        return ComponentType.INBOUND.equals(replacement.getComponentType());
    }
}
