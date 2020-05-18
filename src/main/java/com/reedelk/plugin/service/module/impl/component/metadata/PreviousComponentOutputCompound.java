package com.reedelk.plugin.service.module.impl.component.metadata;

import com.reedelk.plugin.service.module.impl.component.completion.CompletionFinder;
import com.reedelk.plugin.service.module.impl.component.completion.Suggestion;
import com.reedelk.plugin.service.module.impl.component.completion.TypeAndTries;
import com.reedelk.runtime.api.message.MessageAttributes;
import com.reedelk.runtime.api.message.MessagePayload;

import java.util.Collection;
import java.util.List;

public class PreviousComponentOutputCompound implements PreviousComponentOutput {

    private final PreviousComponentOutput attributes;
    private final PreviousComponentOutput payload;

    public PreviousComponentOutputCompound(PreviousComponentOutput attributes, PreviousComponentOutput payload) {
        this.attributes = attributes;
        this.payload = payload;
    }

    @Override
    public Collection<Suggestion> buildDynamicSuggestions(Suggestion suggestion, TypeAndTries typeAndTrieMap, boolean flatten) {
        String suggestionType = suggestion.getReturnType();
        if (MessageAttributes.class.getName().equals(suggestionType)) {
            return attributes.buildDynamicSuggestions(suggestion, typeAndTrieMap, flatten);
        } else if (MessagePayload.class.getName().equals(suggestionType)) {
            return payload.buildDynamicSuggestions(suggestion, typeAndTrieMap, flatten);
        }  else {
            throw new IllegalStateException("Resolve must be called only if the suggestion type is dynamic");
        }
    }

    @Override
    public String description() {
        return payload.description();
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
