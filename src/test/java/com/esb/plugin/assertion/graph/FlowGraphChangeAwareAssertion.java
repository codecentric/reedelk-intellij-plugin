package com.esb.plugin.assertion.graph;

import com.esb.plugin.graph.FlowGraphChangeAware;

import static org.assertj.core.api.Assertions.assertThat;

public class FlowGraphChangeAwareAssertion extends FlowGraphAssertion {

    private FlowGraphChangeAware changeAwareGraph;

    public FlowGraphChangeAwareAssertion(FlowGraphChangeAware graph) {
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
