package com.reedelk.plugin.service.module.impl.component.completion;

import com.reedelk.plugin.service.module.impl.component.metadata.PreviousComponentOutput;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.reedelk.runtime.api.commons.StringUtils.EMPTY;

public class CompletionFinder {

    private final TypeAndTries typeAndTrieMap;

    public CompletionFinder(TypeAndTries typeAndTrieMap) {
        this.typeAndTrieMap = typeAndTrieMap;
    }

    // Finds all the suggestion starting from root type.
    public Collection<Suggestion> find(Trie root, PreviousComponentOutput previousOutput) {
        return find(root, new String[]{EMPTY}, previousOutput);
    }

    public Collection<Suggestion> find(Trie root, String[] tokens, PreviousComponentOutput previousOutput) {
        Trie current = root;
        Collection<Suggestion> autocompleteResults = new ArrayList<>();

        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i];

            if (current == null) {
                autocompleteResults = new ArrayList<>();

            } else if (i == tokens.length - 1) {
                autocompleteResults = autocomplete(current, token, previousOutput, true);

            } else {
                Collection<Suggestion> suggestions = autocomplete(current, token, previousOutput, false);
                List<Trie> exactMatchTries = new ArrayList<>();
                for (Suggestion suggestion : suggestions) {
                    // We only need to find exact matches. If there are no exact matches,
                    // we can not move forward with the autocomplete.
                    if (suggestion.getLookupToken().equals(token)) {
                        Trie trie = suggestion.getReturnType().resolve(typeAndTrieMap);
                        exactMatchTries.add(trie);
                    }
                }

                // We must stop if there are no exact matches.
                if (exactMatchTries.isEmpty()) break;

                // If there is at least one exact match, we can move forward to the
                // next token with the autocomplete.
                current = new TrieMultipleWrapper(exactMatchTries);
            }
        }

        return autocompleteResults;
    }

    private Collection<Suggestion> autocomplete(Trie current, String token, PreviousComponentOutput descriptor, boolean flatten) {
        Collection<Suggestion> suggestions = current.autocomplete(token, typeAndTrieMap);
        List<Suggestion> withDynamicSuggestions = new ArrayList<>();
        for (Suggestion suggestion : suggestions) {
            if (TypeDynamicUtils.is(suggestion)) {
                Collection<Suggestion> dynamicSuggestions =
                        descriptor.buildDynamicSuggestions(suggestion, typeAndTrieMap, flatten);
                withDynamicSuggestions.addAll(dynamicSuggestions);
            } else {
                withDynamicSuggestions.add(suggestion);
            }
        }
        return withDynamicSuggestions;
    }
}
