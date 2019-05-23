package com.esb.plugin.assertion.graph;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;

import static org.assertj.core.api.Assertions.assertThat;

public class RootAssertion {

    private final FlowGraph graph;
    private final FlowGraphAssertion parent;

    public RootAssertion(FlowGraph graph, FlowGraphAssertion parent) {
        this.graph = graph;
        this.parent = parent;
    }

    public RootAssertion is(GraphNode target) {
        assertThat(graph.root()).isEqualTo(target);
        return this;
    }

    public FlowGraphAssertion and() {
        return parent;
    }
}
