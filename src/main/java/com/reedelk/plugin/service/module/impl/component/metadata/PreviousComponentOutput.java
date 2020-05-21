package com.reedelk.plugin.service.module.impl.component.metadata;

import com.reedelk.plugin.service.module.impl.component.completion.Suggestion;
import com.reedelk.plugin.service.module.impl.component.completion.SuggestionFinder;
import com.reedelk.plugin.service.module.impl.component.completion.TypeAndTries;

import java.util.Collection;
import java.util.List;

public interface PreviousComponentOutput {

    String description();

    MetadataTypeDTO mapAttributes(SuggestionFinder suggestionFinder,
                                  TypeAndTries typeAndTries);

    List<MetadataTypeDTO> mapPayload(SuggestionFinder suggestionFinder,
                                     TypeAndTries typeAndTries);

    Collection<Suggestion> buildDynamicSuggestions(SuggestionFinder suggestionFinder,
                                                   Suggestion suggestion,
                                                   TypeAndTries typeAndTrieMap,
                                                   boolean flatten);

}
