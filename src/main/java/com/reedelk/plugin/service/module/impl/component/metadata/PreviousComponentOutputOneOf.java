package com.reedelk.plugin.service.module.impl.component.metadata;

import com.reedelk.plugin.service.module.impl.component.completion.CompletionFinder;
import com.reedelk.plugin.service.module.impl.component.completion.Suggestion;
import com.reedelk.plugin.service.module.impl.component.completion.TypeAndTries;
import com.reedelk.runtime.api.commons.StringUtils;

import java.util.*;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

// Used by router or try/catch.
public class PreviousComponentOutputOneOf extends AbstractPreviousComponentOutput {

    private final List<PreviousComponentOutput> outputs;

    public PreviousComponentOutputOneOf(List<PreviousComponentOutput> outputs) {
        this.outputs = outputs;
    }

    @Override
    public Collection<Suggestion> buildDynamicSuggestions(Suggestion suggestion, TypeAndTries typeAndTrieMap, boolean flatten) {
        return Collections.emptyList();
    }

    @Override
    public String description() {
        return StringUtils.EMPTY;
    }

    @Override
    public MetadataTypeDTO mapAttributes(CompletionFinder completionFinder, TypeAndTries typeAndTries) {
        List<MetadataTypeDTO> attributesToMerge = outputs.stream()
                .map(previousComponentOutput -> previousComponentOutput.mapAttributes(completionFinder, typeAndTries))
                .collect(toList());
        return PreviousComponentOutputDefault.mergeMetadataTypes(attributesToMerge);
    }

    @Override
    public List<MetadataTypeDTO> mapPayload(CompletionFinder completionFinder, TypeAndTries typeAndTries) {
        List<MetadataTypeDTO> payloads = new ArrayList<>();
        for (PreviousComponentOutput componentOutput : outputs) {
            List<MetadataTypeDTO> metadata = componentOutput.mapPayload(completionFinder, typeAndTries);
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
