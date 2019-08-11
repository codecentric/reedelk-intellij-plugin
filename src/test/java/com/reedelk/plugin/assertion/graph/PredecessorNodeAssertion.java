package com.reedelk.plugin.assertion.graph;

import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.node.GraphNode;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class PredecessorNodeAssertion {

    private final GraphNode node;
    private final FlowGraph graph;
    private final FlowGraphAssertion parent;

    PredecessorNodeAssertion(FlowGraph graph, GraphNode node, FlowGraphAssertion parent) {
        this.parent = parent;
        this.graph = graph;
        this.node = node;
    }

    public PredecessorNodeAssertion containsExactly(GraphNode... expectedNodes) {
        List<GraphNode> predecessors = graph.predecessors(node);
        assertThat(predecessors).containsExactlyInAnyOrder(expectedNodes);
        return this;
    }

    public FlowGraphAssertion and() {
        return parent;
    }
}
