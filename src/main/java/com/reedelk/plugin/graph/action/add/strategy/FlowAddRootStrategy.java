package com.reedelk.plugin.graph.action.add.strategy;

import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.action.Strategy;
import com.reedelk.plugin.graph.node.GraphNode;

import static com.reedelk.plugin.component.domain.ComponentClass.INBOUND;

public class FlowAddRootStrategy implements Strategy {

    private final FlowGraph graph;

    FlowAddRootStrategy(FlowGraph graph) {
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
