package com.esb.plugin.assertion;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;

import static org.assertj.core.api.Assertions.assertThat;

public class RootAssertion {

    private final FlowGraph graph;
    private final GraphAssertion parent;

    public RootAssertion(FlowGraph graph, GraphAssertion parent) {
        this.graph = graph;
        this.parent = parent;
    }

    public RootAssertion is(GraphNode target) {
        assertThat(graph.root()).isEqualTo(target);
        return this;
    }

    public GraphAssertion and() {
        return parent;
    }
}
