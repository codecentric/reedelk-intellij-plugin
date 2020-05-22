package com.reedelk.plugin.service.module.impl.component.metadata;

import com.reedelk.plugin.service.module.impl.component.completion.*;
import com.reedelk.runtime.api.message.MessageAttributes;
import com.reedelk.runtime.api.message.MessagePayload;
import org.jetbrains.annotations.NotNull;

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
    public Collection<Suggestion> buildDynamicSuggestions(@NotNull SuggestionFinder suggestionFinder,
                                                          @NotNull Suggestion suggestion,
                                                          @NotNull TypeAndTries typeAndTrieMap,
                                                          @NotNull FlattenStrategy flattenStrategy) {
        TypeProxy suggestionType = suggestion.getReturnType();
        if (MessageAttributes.class.getName().equals(suggestionType.getTypeFullyQualifiedName())) {
            return attributes.buildDynamicSuggestions(suggestionFinder, suggestion, typeAndTrieMap, flattenStrategy);
        } else if (MessagePayload.class.getName().equals(suggestionType.getTypeFullyQualifiedName())) {
            return payload.buildDynamicSuggestions(suggestionFinder, suggestion, typeAndTrieMap, flattenStrategy);
        }  else {
            throw new IllegalStateException("Resolve must be called only if the suggestion type is dynamic");
        }
    }

    @Override
    public String description() {
        return payload.description();
    }

    @Override
    public MetadataTypeDTO mapAttributes(@NotNull SuggestionFinder suggestionFinder, @NotNull TypeAndTries typeAndTries) {
        return attributes.mapAttributes(suggestionFinder, typeAndTries);
    }

    @Override
    public List<MetadataTypeDTO> mapPayload(@NotNull SuggestionFinder suggestionFinder, @NotNull TypeAndTries typeAndTries) {
        return payload.mapPayload(suggestionFinder, typeAndTries);
    }
}
