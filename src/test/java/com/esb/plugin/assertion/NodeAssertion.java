package com.esb.plugin.assertion;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;

import static org.assertj.core.api.Assertions.assertThat;

public class NodeAssertion {

    private final GraphNode node;
    private final FlowGraph graph;
    private final GraphAssertion parent;

    NodeAssertion(FlowGraph graph, GraphNode node, GraphAssertion parent) {
        this.parent = parent;
        this.graph = graph;
        this.node = node;
    }

    public NodeAssertion isEqualTo(GraphNode expectedNode) {
        assertThat(this.node).isEqualTo(expectedNode);
        return this;
    }

    public GraphAssertion and() {
        return parent;
    }

}
