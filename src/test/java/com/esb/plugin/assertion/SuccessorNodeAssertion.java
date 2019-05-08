package com.esb.plugin.assertion;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class SuccessorNodeAssertion {

    private final GraphNode node;
    private final FlowGraph graph;
    private final GraphAssertion parent;

    SuccessorNodeAssertion(FlowGraph graph, GraphNode node, GraphAssertion parent) {
        this.parent = parent;
        this.graph = graph;
        this.node = node;
    }

    public SuccessorNodeAssertion areExactly(GraphNode... expectedNodes) {
        List<GraphNode> successors = graph.successors(node);
        assertThat(successors).containsExactlyInAnyOrder(expectedNodes);
        return this;
    }

    public SuccessorNodeAssertion isOnly(GraphNode expectedNode) {
        List<GraphNode> successors = graph.successors(node);
        assertThat(successors).containsExactly(expectedNode);
        return this;
    }

    public SuccessorNodeAssertion isEmpty() {
        List<GraphNode> successors = graph.successors(node);
        assertThat(successors).isEmpty();
        return this;
    }

    public GraphAssertion and() {
        return parent;
    }

}
