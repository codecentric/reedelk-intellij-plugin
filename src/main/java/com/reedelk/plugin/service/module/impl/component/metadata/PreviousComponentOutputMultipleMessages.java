package com.reedelk.plugin.service.module.impl.component.metadata;

import com.reedelk.plugin.service.module.impl.component.completion.Suggestion;
import com.reedelk.plugin.service.module.impl.component.completion.SuggestionFinder;
import com.reedelk.plugin.service.module.impl.component.completion.TypeAndTries;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class PreviousComponentOutputMultipleMessages implements PreviousComponentOutput {

    @Override
    public Collection<Suggestion> buildDynamicSuggestions(SuggestionFinder suggestionFinder, Suggestion suggestion, TypeAndTries typeAndTrieMap, boolean flatten) {
        return Collections.emptyList();
    }

    @Override
    public String description() {
        throw new UnsupportedOperationException();
    }

    @Override
    public MetadataTypeDTO mapAttributes(SuggestionFinder suggestionFinder, TypeAndTries typeAndTries) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<MetadataTypeDTO> mapPayload(SuggestionFinder suggestionFinder, TypeAndTries typeAndTries) {
        throw new UnsupportedOperationException();
    }
}
