package de.codecentric.reedelk.plugin.service.module.impl.component.metadata;

import de.codecentric.reedelk.plugin.service.module.impl.component.completion.Suggestion;
import de.codecentric.reedelk.plugin.service.module.impl.component.completion.SuggestionFinder;
import de.codecentric.reedelk.plugin.service.module.impl.component.completion.TypeAndTries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public interface PreviousComponentOutput {

    // Used by metadata service
    @Nullable
    String description();

    // Used by metadata service
    MetadataTypeDTO mapAttributes(@NotNull SuggestionFinder suggester,
                                  @NotNull TypeAndTries typeAndTries);

    // Used by metadata service
    List<MetadataTypeDTO> mapPayload(@NotNull SuggestionFinder suggester,
                                     @NotNull TypeAndTries typeAndTries);

    // Used by completion service.
    Collection<Suggestion> buildDynamicSuggestions(@NotNull SuggestionFinder suggester,
                                                   @NotNull Suggestion suggestion,
                                                   @NotNull TypeAndTries typeAndTrieMap);
}
