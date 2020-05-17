package com.reedelk.plugin.service.module.impl.component.metadata;

import com.reedelk.plugin.service.module.impl.component.completion.CompletionFinder;
import com.reedelk.plugin.service.module.impl.component.completion.Suggestion;
import com.reedelk.plugin.service.module.impl.component.completion.TypeAndTries;

import java.util.Collection;
import java.util.List;

public class PreviousComponentOutputCompound implements PreviousComponentOutput {

    private final PreviousComponentOutput attributes;
    private final PreviousComponentOutput payload;
    private final String description;

    public PreviousComponentOutputCompound(PreviousComponentOutput attributes, PreviousComponentOutput payload) {
        this.attributes = attributes;
        this.payload = payload;
        this.description = "Fixme"; // TODO: Fixme
    }

    @Override
    public Collection<Suggestion> buildDynamicSuggestions(Suggestion suggestion, TypeAndTries typeAndTrieMap, boolean flatten) {
        // TODO: Fixme
        Collection<Suggestion> attributesSuggetions = attributes.buildDynamicSuggestions(suggestion, typeAndTrieMap, flatten);
        Collection<Suggestion> payloadSuggestions = payload.buildDynamicSuggestions(suggestion, typeAndTrieMap, flatten);
        attributesSuggetions.addAll(payloadSuggestions);
        return attributesSuggetions;
    }

    @Override
    public String description() {
        return description;
    }

    @Override
    public MetadataTypeDTO mapAttributes(CompletionFinder completionFinder, TypeAndTries typeAndTries) {
        return attributes.mapAttributes(completionFinder, typeAndTries);
    }

    @Override
    public List<MetadataTypeDTO> mapPayload(CompletionFinder completionFinder, TypeAndTries typeAndTries) {
        return payload.mapPayload(completionFinder, typeAndTries);
    }
}
