package com.reedelk.plugin.service.module.impl.component.completion;

import com.reedelk.plugin.exception.PluginException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;

public enum FlattenStrategy {

    /**
     * Takes a group of suggestions which refer to the same lookup string and collapses them into one
     * single suggestion with type equal to a comma separated list of all the suggestions in the group.
     */
    BY_LOOKUP_TOKEN {
        @Override
        public Collection<Suggestion> flatten(Collection<Suggestion> suggestions, TypeAndTries typeAndTrieMap) {
            if (suggestions.isEmpty()) return suggestions;
            // no need to flatten
            if (suggestions.size() == 1) return singletonList(suggestions.iterator().next());

            Map<String, List<Suggestion>> suggestionsGroupedByLookupToken = suggestions
                    .stream()
                    .collect(Collectors.groupingBy(Suggestion::getLookupToken));

            Collection<Suggestion> flattenedSuggestions = new ArrayList<>();
            suggestionsGroupedByLookupToken.forEach((lookupToken, suggestionsForLookupToken) -> {

                // If there is only one suggestion, we don't apply the flatten on the types.
                if (suggestionsForLookupToken.size() == 1) {
                    flattenedSuggestions.add(suggestionsForLookupToken.iterator().next());
                    return;
                }

                String joinedDisplayValues = suggestionsForLookupToken
                        .stream()
                        .map(Suggestion::getReturnTypeDisplayValue)
                        .collect(Collectors.joining(","));

                // There must be at least one suggestion for this group.
                Suggestion suggestion = suggestionsForLookupToken
                        .stream()
                        .findAny()
                        .orElseThrow(() -> new PluginException("Expected at least one dynamic suggestion."));

                Suggestion groupedSuggestion = Suggestion.create(suggestion.getType())
                        .insertValue(suggestion.getInsertValue())
                        .tailText(suggestion.getTailText())
                        .lookupToken(suggestion.getLookupToken())
                        // The return type for 'flattened' suggestions is never used because this suggestion is only created for a terminal token.
                        .returnType(TypeProxy.FLATTENED)
                        .returnTypeDisplayValue(String.join(",", joinedDisplayValues))
                        .build();

                flattenedSuggestions.add(groupedSuggestion);
            });

            return flattenedSuggestions;
        }
    };

    public abstract Collection<Suggestion> flatten(Collection<Suggestion> suggestions, TypeAndTries typeAndTrieMap);
}
