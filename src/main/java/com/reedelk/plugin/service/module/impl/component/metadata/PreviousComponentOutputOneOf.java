package com.reedelk.plugin.service.module.impl.component.metadata;

import com.reedelk.plugin.service.module.impl.component.completion.Suggestion;
import com.reedelk.plugin.service.module.impl.component.completion.SuggestionFinder;
import com.reedelk.plugin.service.module.impl.component.completion.TypeAndTries;
import com.reedelk.plugin.service.module.impl.component.completion.TypeProxy;
import com.reedelk.runtime.api.commons.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

// Used by router or try/catch.
public class PreviousComponentOutputOneOf extends AbstractPreviousComponentOutput {

    private final List<PreviousComponentOutput> outputs;

    public PreviousComponentOutputOneOf(List<PreviousComponentOutput> outputs) {
        this.outputs = outputs;
    }

    @Override
    public Collection<Suggestion> buildDynamicSuggestions(@NotNull SuggestionFinder suggester,
                                                          @NotNull Suggestion suggestion,
                                                          @NotNull TypeAndTries typeAndTrieMap) {
        List<Suggestion> suggestions = new ArrayList<>();
        for (PreviousComponentOutput output : outputs) {
            Collection<Suggestion> currentSuggestions =
                    output.buildDynamicSuggestions(suggester, suggestion, typeAndTrieMap);
            suggestions.addAll(currentSuggestions);
        }
        return suggestions;
    }

    @Override
    public String description() {
        return StringUtils.EMPTY;
    }

    @Override
    public MetadataTypeDTO mapAttributes(@NotNull SuggestionFinder suggester, @NotNull TypeAndTries typeAndTries) {
        List<MetadataTypeDTO> attributesToMerge = outputs.stream()
                .map(previousComponentOutput -> previousComponentOutput.mapAttributes(suggester, typeAndTries))
                .collect(toList());
        return PreviousComponentOutputDefault.mergeMetadataTypes(attributesToMerge, typeAndTries);
    }

    @Override
    public List<MetadataTypeDTO> mapPayload(@NotNull SuggestionFinder suggester, @NotNull TypeAndTries typeAndTries) {
        List<MetadataTypeDTO> payloads = new ArrayList<>();
        for (PreviousComponentOutput componentOutput : outputs) {
            List<MetadataTypeDTO> metadata = componentOutput.mapPayload(suggester, typeAndTries);
            payloads.addAll(metadata);
        }

        // Logic should be if all types have the same type
        Map<TypeProxy, List<MetadataTypeDTO>> collect = payloads
                .stream()
                .collect(groupingBy(MetadataTypeDTO::getTypeProxy));

        // We need to only return *distinct* payload types (i.e: we don't want to return String,String)
        List<MetadataTypeDTO> onlyOnes = new ArrayList<>();
        collect.forEach((key, value) -> onlyOnes.add(value.iterator().next()));
        return onlyOnes;
    }
}
