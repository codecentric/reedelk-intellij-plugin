package de.codecentric.reedelk.plugin.assertion.graph;

import de.codecentric.reedelk.plugin.graph.FlowGraph;
import de.codecentric.reedelk.plugin.graph.node.GraphNode;
import de.codecentric.reedelk.plugin.graph.node.ScopedGraphNode;

import static org.assertj.core.api.Assertions.assertThat;

public class FlowGraphAssertion {

    private FlowGraph graph;

    public FlowGraphAssertion(FlowGraph graph) {
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

    public FlowGraphAssertion nodesCountIs(int expectedCount) {
        assertThat(graph.nodesCount()).isEqualTo(expectedCount);
        return this;
    }

    public FlowGraphAssertion isEmpty() {
        assertThat(graph.isEmpty()).isTrue();
        return this;
    }

    public FlowGraphAssertion contains(GraphNode node) {
        assertThat(graph.nodes()).contains(node);
        return this;
    }
}
