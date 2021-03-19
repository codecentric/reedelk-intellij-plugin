package de.codecentric.reedelk.plugin.assertion;

import de.codecentric.reedelk.module.descriptor.model.component.ComponentDataHolder;
import de.codecentric.reedelk.plugin.assertion.commons.MapAssertion;
import de.codecentric.reedelk.plugin.assertion.component.ComponentDataHolderAssertion;
import de.codecentric.reedelk.plugin.assertion.graph.FlowGraphAssertion;
import de.codecentric.reedelk.plugin.assertion.graph.FlowGraphChangeAwareAssertion;
import de.codecentric.reedelk.plugin.assertion.metadata.MetadataTypeDTOAssertion;
import de.codecentric.reedelk.plugin.assertion.metadata.MetadataTypeDTOListAssertion;
import de.codecentric.reedelk.plugin.assertion.suggestion.SuggestionAssertion;
import de.codecentric.reedelk.plugin.assertion.suggestion.SuggestionsAssertion;
import de.codecentric.reedelk.plugin.graph.FlowGraph;
import de.codecentric.reedelk.plugin.graph.FlowGraphChangeAware;
import de.codecentric.reedelk.plugin.service.module.impl.component.completion.Suggestion;
import de.codecentric.reedelk.plugin.service.module.impl.component.metadata.MetadataTypeDTO;

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
