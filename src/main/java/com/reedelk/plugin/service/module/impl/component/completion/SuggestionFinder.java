package com.reedelk.plugin.service.module.impl.component.completion;

import com.reedelk.plugin.service.module.impl.component.metadata.PreviousComponentOutput;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.reedelk.runtime.api.commons.StringUtils.EMPTY;

public class SuggestionFinder {

    private final TypeAndTries typeAndTrieMap;

    public SuggestionFinder(TypeAndTries typeAndTrieMap) {
        this.typeAndTrieMap = typeAndTrieMap;
    }

    // Finds all the suggestion starting from root type.
    public Collection<Suggestion> suggest(Trie root, PreviousComponentOutput previousOutput) {
        return suggest(root, new String[]{EMPTY}, previousOutput);
    }

    public Collection<Suggestion> suggest(Trie root, String[] tokens, PreviousComponentOutput previousOutput) {

        Trie currentTrie = root;

        Collection<Suggestion> suggestionResults = new ArrayList<>();

        for (int i = 0; i < tokens.length; i++) {

            String currentToken = tokens[i];

            if (currentTrie == null) {

                suggestionResults = new ArrayList<>();

            } else if (i == tokens.length - 1) {

                suggestionResults = autocomplete(currentTrie, currentToken, previousOutput, true);

            } else {
                Collection<Suggestion> suggestions = autocomplete(currentTrie, currentToken, previousOutput, false);

                List<Trie> exactMatchTries = new ArrayList<>();

                for (Suggestion suggestion : suggestions) {
                    // We only need to find exact matches. If there are no exact matches,
                    // we can not move forward with the autocomplete.
                    if (suggestion.getLookupToken().equals(currentToken)) {
                        Trie trie = suggestion.getReturnType().resolve(typeAndTrieMap);
                        exactMatchTries.add(trie);
                    }
                }

                // We must stop if there are no exact matches.
                if (exactMatchTries.isEmpty()) break;

                // If there is at least one exact match, we can move forward to the
                // next token with the autocomplete.
                currentTrie = new TrieMultipleWrapper(exactMatchTries);

            }
        }

        return suggestionResults;
    }

    private Collection<Suggestion> autocomplete(Trie current, String token, PreviousComponentOutput descriptor, boolean flatten) {
        Collection<Suggestion> suggestions = current.autocomplete(token, typeAndTrieMap);
        List<Suggestion> withDynamicSuggestions = new ArrayList<>();
        for (Suggestion suggestion : suggestions) {
            if (suggestion.getReturnType().isDynamic()) {
                Collection<Suggestion> dynamicSuggestions =
                        descriptor.buildDynamicSuggestions(this, suggestion, typeAndTrieMap, flatten);
                withDynamicSuggestions.addAll(dynamicSuggestions);
            } else {
                withDynamicSuggestions.add(suggestion);
            }
        }
        return withDynamicSuggestions;
    }
}
