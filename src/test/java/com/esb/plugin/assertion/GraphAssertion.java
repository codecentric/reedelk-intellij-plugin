package com.esb.plugin.assertion;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.ScopedGraphNode;

import static org.assertj.core.api.Assertions.assertThat;

public class GraphAssertion {

    private FlowGraph graph;

    GraphAssertion(FlowGraph graph) {
        this.graph = graph;
    }

    public SuccessorNodeAssertion successorsOf(GraphNode node) {
        return new SuccessorNodeAssertion(graph, node, this);
    }

    public PredecessorNodeAssertion predecessorOf(GraphNode node) {
        return new PredecessorNodeAssertion(graph, node, this);
    }

    public RootAssertion root() {
        return new RootAssertion(graph, this);
    }

    public NodeAssertion node(GraphNode node) {
        return new NodeAssertion(node, this);
    }

    public ScopedNodeAssertion node(ScopedGraphNode node) {
        return new ScopedNodeAssertion(node, this);
    }

    public GraphAssertion nodesCountIs(int expectedCount) {
        assertThat(graph.nodesCount()).isEqualTo(expectedCount);
        return this;
    }

}
