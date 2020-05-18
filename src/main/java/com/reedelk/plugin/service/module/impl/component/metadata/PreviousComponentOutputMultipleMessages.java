package com.reedelk.plugin.service.module.impl.component.metadata;

import com.reedelk.plugin.service.module.impl.component.completion.CompletionFinder;
import com.reedelk.plugin.service.module.impl.component.completion.Suggestion;
import com.reedelk.plugin.service.module.impl.component.completion.TypeAndTries;

import java.util.Collection;
import java.util.List;

// TODO: Fixme
public class PreviousComponentOutputMultipleMessages implements PreviousComponentOutput {

    @Override
    public Collection<Suggestion> buildDynamicSuggestions(Suggestion suggestion, TypeAndTries typeAndTrieMap, boolean flatten) {
        return null;
    }

    @Override
    public String description() {
        return null;
    }

    @Override
    public MetadataTypeDTO mapAttributes(CompletionFinder completionFinder, TypeAndTries typeAndTries) {
        return null;
    }

    @Override
    public List<MetadataTypeDTO> mapPayload(CompletionFinder completionFinder, TypeAndTries typeAndTries) {
        return null;
    }
}
