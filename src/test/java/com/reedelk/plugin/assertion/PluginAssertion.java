package com.reedelk.plugin.assertion;

import com.reedelk.module.descriptor.model.ComponentDataHolder;
import com.reedelk.plugin.assertion.commons.MapAssertion;
import com.reedelk.plugin.assertion.component.ComponentDataHolderAssertion;
import com.reedelk.plugin.assertion.graph.FlowGraphAssertion;
import com.reedelk.plugin.assertion.graph.FlowGraphChangeAwareAssertion;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.FlowGraphChangeAware;

import java.util.Map;

public class PluginAssertion {

    public static MapAssertion assertThat(Map<String, ?> map) {
        return new MapAssertion(map);
    }

    public static FlowGraphAssertion assertThat(FlowGraph graph) {
        return new FlowGraphAssertion(graph);
    }

    public static FlowGraphChangeAwareAssertion assertThat(FlowGraphChangeAware graph) {
        return new FlowGraphChangeAwareAssertion(graph);
    }

    public static ComponentDataHolderAssertion assertThat(ComponentDataHolder dataHolder) {
        return new ComponentDataHolderAssertion(dataHolder);
    }
}
