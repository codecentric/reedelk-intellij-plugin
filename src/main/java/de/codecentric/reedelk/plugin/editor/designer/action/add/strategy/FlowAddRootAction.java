package de.codecentric.reedelk.plugin.editor.designer.action.add.strategy;

import de.codecentric.reedelk.plugin.graph.FlowGraph;
import de.codecentric.reedelk.plugin.graph.node.GraphNode;
import de.codecentric.reedelk.module.descriptor.model.component.ComponentType;
import de.codecentric.reedelk.plugin.editor.designer.action.ActionStrategy;

public class FlowAddRootAction implements ActionStrategy {

    private final FlowGraph graph;

    FlowAddRootAction(FlowGraph graph) {
        this.graph = graph;
    }

    @Override
    public void execute(GraphNode node) {
        graph.root(node);
    }

    @Override
    public boolean applicableOn(GraphNode node) {
        // Only inbound components can be added as root inside flows.
        return ComponentType.INBOUND.equals(node.getComponentType());
    }
}
