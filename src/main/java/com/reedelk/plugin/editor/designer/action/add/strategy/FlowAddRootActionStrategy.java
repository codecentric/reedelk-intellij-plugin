package com.reedelk.plugin.editor.designer.action.add.strategy;

import com.reedelk.component.descriptor.ComponentType;
import com.reedelk.plugin.editor.designer.action.ActionStrategy;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.node.GraphNode;

public class FlowAddRootActionStrategy implements ActionStrategy {

    private final FlowGraph graph;

    FlowAddRootActionStrategy(FlowGraph graph) {
        this.graph = graph;
    }

    @Override
    public void execute(GraphNode node) {
        graph.root(node);
    }

    @Override
    public boolean applicableOn(GraphNode node) {
        // Only inbound components can be added as root inside flows.
        return ComponentType.INBOUND.equals(node.getComponentClass());
    }
}
