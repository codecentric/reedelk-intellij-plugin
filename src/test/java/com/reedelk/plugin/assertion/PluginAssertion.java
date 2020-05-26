package com.reedelk.plugin.assertion;

import com.reedelk.module.descriptor.model.component.ComponentDataHolder;
import com.reedelk.plugin.assertion.commons.MapAssertion;
import com.reedelk.plugin.assertion.component.ComponentDataHolderAssertion;
import com.reedelk.plugin.assertion.graph.FlowGraphAssertion;
import com.reedelk.plugin.assertion.graph.FlowGraphChangeAwareAssertion;
import com.reedelk.plugin.assertion.metadata.MetadataTypeDTOAssertion;
import com.reedelk.plugin.assertion.metadata.MetadataTypeDTOListAssertion;
import com.reedelk.plugin.assertion.suggestion.SuggestionAssertion;
import com.reedelk.plugin.assertion.suggestion.SuggestionsAssertion;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.FlowGraphChangeAware;
import com.reedelk.plugin.service.module.impl.component.completion.Suggestion;
import com.reedelk.plugin.service.module.impl.component.metadata.MetadataTypeDTO;

import java.util.Collection;
import java.util.List;
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

    public static SuggestionAssertion assertThat(Suggestion suggestion) {
        return new SuggestionAssertion(suggestion);
    }

    public static SuggestionsAssertion assertThat(Collection<Suggestion> suggestions) {
        return new SuggestionsAssertion(suggestions);
    }

    public static MetadataTypeDTOAssertion assertThat(MetadataTypeDTO metadataType) {
        return new MetadataTypeDTOAssertion(metadataType);
    }

    public static MetadataTypeDTOListAssertion assertThat(List<MetadataTypeDTO> metadataTypeDTOList) {
        return new MetadataTypeDTOListAssertion(metadataTypeDTOList);
    }
}
