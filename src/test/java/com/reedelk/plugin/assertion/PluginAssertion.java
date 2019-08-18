package com.reedelk.plugin.assertion;

import com.reedelk.plugin.assertion.component.ComponentDataHolderAssertion;
import com.reedelk.plugin.assertion.component.ComponentDescriptorAssertion;
import com.reedelk.plugin.assertion.component.ComponentPropertyDescriptorAssertion;
import com.reedelk.plugin.assertion.graph.FlowGraphAssertion;
import com.reedelk.plugin.assertion.graph.FlowGraphChangeAwareAssertion;
import com.reedelk.plugin.assertion.trie.TrieAssertion;
import com.reedelk.plugin.component.domain.ComponentDataHolder;
import com.reedelk.plugin.component.domain.ComponentDescriptor;
import com.reedelk.plugin.component.domain.ComponentPropertyDescriptor;
import com.reedelk.plugin.editor.properties.widget.input.script.suggestion.SuggestionTree;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.FlowGraphChangeAware;

public class PluginAssertion {

    public static TrieAssertion assertThat(SuggestionTree suggestionTree) {
        return new TrieAssertion(suggestionTree);
    }

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

    public static ComponentDataHolderAssertion assertThat(ComponentDataHolder dataHolder) {
        return new ComponentDataHolderAssertion(dataHolder);
    }
}
