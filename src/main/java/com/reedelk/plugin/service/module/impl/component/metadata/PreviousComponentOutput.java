package com.reedelk.plugin.service.module.impl.component.metadata;

import com.reedelk.plugin.service.module.impl.component.completion.Suggestion;
import com.reedelk.plugin.service.module.impl.component.completion.SuggestionFinder;
import com.reedelk.plugin.service.module.impl.component.completion.TypeAndTries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public interface PreviousComponentOutput {

    @Nullable String description();

    MetadataTypeDTO mapAttributes(@NotNull SuggestionFinder suggestionFinder,
                                  @NotNull TypeAndTries typeAndTries);

    List<MetadataTypeDTO> mapPayload(@NotNull SuggestionFinder suggestionFinder,
                                     @NotNull TypeAndTries typeAndTries);

    Collection<Suggestion> buildDynamicSuggestions(@NotNull SuggestionFinder suggestionFinder,
                                                   @NotNull Suggestion suggestion,
                                                   @NotNull TypeAndTries typeAndTrieMap);
}
