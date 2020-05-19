package com.reedelk.plugin.service.module.impl.component.metadata;

import com.reedelk.plugin.service.module.impl.component.completion.CompletionFinder;
import com.reedelk.plugin.service.module.impl.component.completion.Suggestion;
import com.reedelk.plugin.service.module.impl.component.completion.TypeAndTries;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class PreviousComponentOutputMultipleMessages implements PreviousComponentOutput {

    @Override
    public Collection<Suggestion> buildDynamicSuggestions(CompletionFinder completionFinder, Suggestion suggestion, TypeAndTries typeAndTrieMap, boolean flatten) {
        return Collections.emptyList();
    }

    @Override
    public String description() {
        throw new UnsupportedOperationException();
    }

    @Override
    public MetadataTypeDTO mapAttributes(CompletionFinder completionFinder, TypeAndTries typeAndTries) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<MetadataTypeDTO> mapPayload(CompletionFinder completionFinder, TypeAndTries typeAndTries) {
        throw new UnsupportedOperationException();
    }
}
