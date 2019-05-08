package com.esb.plugin.assertion;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.FlowGraphChangeAware;

public class PluginAssertion {

    public static GraphAssertion assertThat(FlowGraph graph) {
        return new GraphAssertion(graph);
    }

    public static FlowGraphChangeAwareAssertion assertThat(FlowGraphChangeAware graph) {
        return new FlowGraphChangeAwareAssertion(graph);
    }
}
