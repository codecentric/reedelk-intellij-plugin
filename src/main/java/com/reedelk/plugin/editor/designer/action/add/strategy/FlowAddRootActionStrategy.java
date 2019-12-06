package com.reedelk.plugin.editor.designer.action.add.strategy;

import com.reedelk.plugin.editor.designer.action.ActionStrategy;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.node.GraphNode;

import static com.reedelk.plugin.component.domain.ComponentType.INBOUND;

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
        return INBOUND.equals(node.getComponentClass());
    }
}
