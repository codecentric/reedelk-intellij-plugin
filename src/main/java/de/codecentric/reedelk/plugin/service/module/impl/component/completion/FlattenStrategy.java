package de.codecentric.reedelk.plugin.service.module.impl.component.completion;

import de.codecentric.reedelk.plugin.exception.PluginException;

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
                    // We must group by lookup token and by the tail text because there might be
                    // methods with the same lookup token but with different tail text because
                    // of different parameters arguments.
                    .collect(Collectors.groupingBy(suggestion -> suggestion.getLookupToken() + suggestion.getTailText()));

            Collection<Suggestion> flattenedSuggestions = new ArrayList<>();
            suggestionsGroupedByLookupToken.forEach((lookupTokenAndTailText, suggestionsForLookupToken) -> {

                // If there is only one suggestion, we don't apply the flatten on the types.
                if (suggestionsForLookupToken.size() == 1) {
                    flattenedSuggestions.add(suggestionsForLookupToken.iterator().next());
                    return;
                }

                String joinedDisplayValues = suggestionsForLookupToken
                        .stream()
                        .map(Suggestion::getReturnTypeDisplayValue)
                        .distinct()
                        .collect(Collectors.joining(","));

                String joinedFullyQualifiedNames = suggestionsForLookupToken
                        .stream()
                        .map(suggestion -> suggestion.getReturnType().getTypeFullyQualifiedName())
                        .distinct()
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
                        .returnType(new FlattenedTypeProxy(joinedFullyQualifiedNames, joinedDisplayValues))
                        .returnTypeDisplayValue(joinedDisplayValues)
                        .build();

                flattenedSuggestions.add(groupedSuggestion);
            });

            return flattenedSuggestions;
        }
    };

    public abstract Collection<Suggestion> flatten(Collection<Suggestion> suggestions, TypeAndTries typeAndTrieMap);

    static class FlattenedTypeProxy extends TypeProxyDefault {

        private final String displayName;

        public FlattenedTypeProxy(String typeFullyQualifiedName, String displayName) {
            super(typeFullyQualifiedName);
            this.displayName = displayName;
        }

        @Override
        public String toSimpleName(TypeAndTries typeAndTries) {
            return displayName;
        }
    }
}
