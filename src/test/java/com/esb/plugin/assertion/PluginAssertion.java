package com.esb.plugin.assertion;

import com.esb.plugin.assertion.component.ComponentDescriptorAssertion;
import com.esb.plugin.assertion.component.ComponentPropertyDescriptorAssertion;
import com.esb.plugin.assertion.graph.FlowGraphAssertion;
import com.esb.plugin.assertion.graph.FlowGraphChangeAwareAssertion;
import com.esb.plugin.component.domain.ComponentDescriptor;
import com.esb.plugin.component.domain.ComponentPropertyDescriptor;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.FlowGraphChangeAware;

public class PluginAssertion {

    public static FlowGraphAssertion assertThat(FlowGraph graph) {
        return new FlowGraphAssertion(graph);
    }

    public static FlowGraphChangeAwareAssertion assertThat(FlowGraphChangeAware graph) {
        return new FlowGraphChangeAwareAssertion(graph);
    }

    public static ComponentDescriptorAssertion assertThat(ComponentDescriptor componentDescriptor) {
        return new ComponentDescriptorAssertion(componentDescriptor);
    }

    public static ComponentPropertyDescriptorAssertion assertThat(ComponentPropertyDescriptor propertyDescriptor) {
        return new ComponentPropertyDescriptorAssertion(propertyDescriptor);
    }
}
