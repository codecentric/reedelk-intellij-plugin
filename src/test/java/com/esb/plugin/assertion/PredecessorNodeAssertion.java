package com.esb.plugin.assertion;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class PredecessorNodeAssertion {

    private final GraphNode node;
    private final FlowGraph graph;
    private final GraphAssertion parent;

    PredecessorNodeAssertion(FlowGraph graph, GraphNode node, GraphAssertion parent) {
        this.parent = parent;
        this.graph = graph;
        this.node = node;
    }

    public PredecessorNodeAssertion containsExactly(GraphNode... expectedNodes) {
        List<GraphNode> predecessors = graph.predecessors(node);
        assertThat(predecessors).containsExactlyInAnyOrder(expectedNodes);
        return this;
    }

    public GraphAssertion and() {
        return parent;
    }
}
