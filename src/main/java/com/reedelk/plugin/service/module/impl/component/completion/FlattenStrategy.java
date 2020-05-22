package com.reedelk.plugin.service.module.impl.component.completion;

import com.reedelk.plugin.exception.PluginException;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;

public enum FlattenStrategy {

    NEVER {
        @Override
        public Collection<Suggestion> flatten(Collection<Suggestion> suggestions, TypeAndTries typeAndTrieMap) {
            return suggestions;
        }
    },

    ALL {
        /**
         * Takes a group of suggestions which refer to the same lookup string and collapses them into one
         * single suggestion with type equal to a comma separated list of all the suggestions in the group.
         */
        @Override
        public Collection<Suggestion> flatten(Collection<Suggestion> suggestions, TypeAndTries typeAndTrieMap) {
            if (suggestions.isEmpty()) return suggestions;
            if (suggestions.size() == 1) {
                // no need to flatten
                return Collections.singletonList(suggestions.iterator().next());
            }

            Suggestion suggestion = suggestions.stream()
                    .findAny()
                    .orElseThrow(() -> new PluginException("Expected at least one dynamic suggestion."));

            List<String> possibleTypes = suggestions.stream()
                    .map(theSuggestion -> theSuggestion.getReturnType().toSimpleName(typeAndTrieMap))
                    .collect(toList());

            Suggestion flattenedSuggestions = Suggestion.create(suggestion.getType())
                    .insertValue(suggestion.getInsertValue())
                    .tailText(suggestion.getTailText())
                    .lookupToken(suggestion.getLookupToken())
                    // The return type for 'flattened' suggestions is never used because this suggestion is only created for a terminal token.
                    .returnType(TypeProxy.FLATTENED)
                    .returnTypeDisplayValue(String.join(",", possibleTypes))
                    .build();

            return Collections.singletonList(flattenedSuggestions);
        }
    },

    BY_LOOKUP_TOKEN {
        @Override
        public Collection<Suggestion> flatten(Collection<Suggestion> suggestions, TypeAndTries typeAndTrieMap) {
            return null;
        }
    };

    public abstract Collection<Suggestion> flatten(Collection<Suggestion> suggestions, TypeAndTries typeAndTrieMap);
}
