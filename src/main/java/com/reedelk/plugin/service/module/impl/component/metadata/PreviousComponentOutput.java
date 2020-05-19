package com.reedelk.plugin.service.module.impl.component.metadata;

import com.reedelk.plugin.service.module.impl.component.completion.CompletionFinder;
import com.reedelk.plugin.service.module.impl.component.completion.Suggestion;
import com.reedelk.plugin.service.module.impl.component.completion.TypeAndTries;

import java.util.Collection;
import java.util.List;

public interface PreviousComponentOutput {

    String description();

    MetadataTypeDTO mapAttributes(CompletionFinder completionFinder,
                                  TypeAndTries typeAndTries);

    List<MetadataTypeDTO> mapPayload(CompletionFinder completionFinder,
                                     TypeAndTries typeAndTries);

    Collection<Suggestion> buildDynamicSuggestions(CompletionFinder completionFinder,
                                                   Suggestion suggestion,
                                                   TypeAndTries typeAndTrieMap,
                                                   boolean flatten);

}
