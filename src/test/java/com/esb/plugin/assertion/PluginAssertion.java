package com.esb.plugin.assertion;

import com.esb.plugin.graph.FlowGraph;

public class PluginAssertion {

    public static GraphAssertion assertThat(FlowGraph graph) {
        return new GraphAssertion(graph);
    }
}
