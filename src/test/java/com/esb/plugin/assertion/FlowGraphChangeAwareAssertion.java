package com.esb.plugin.assertion;

import com.esb.plugin.graph.FlowGraphChangeAware;

import static org.assertj.core.api.Assertions.assertThat;

public class FlowGraphChangeAwareAssertion extends GraphAssertion {

    private FlowGraphChangeAware changeAwareGraph;

    FlowGraphChangeAwareAssertion(FlowGraphChangeAware graph) {
        super(graph);
        this.changeAwareGraph = graph;
    }

    public FlowGraphChangeAwareAssertion isChanged() {
        assertThat(changeAwareGraph.isChanged()).isTrue();
        return this;
    }

    public FlowGraphChangeAwareAssertion isNotChanged() {
        assertThat(changeAwareGraph.isChanged()).isFalse();
        return this;
    }
}
