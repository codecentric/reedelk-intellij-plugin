package com.reedelk.plugin.service.module.impl.completion;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CompletionFinder {

    private static final Trie UNKNOWN_TYPE_TRIE = new Trie();

    public static List<Suggestion> find(Trie root, Map<String, Trie> typeAndTrieMap, String[] tokens) {
        Trie current = root;
        List<Suggestion> autocompleteResults = new ArrayList<>();
        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i];
            if (i == tokens.length - 1) {
                autocompleteResults = current.autocomplete(token);
            } else {
                Optional<Suggestion> maybeSuggestion = current.autocomplete(token).stream().findFirst();
                if (maybeSuggestion.isPresent()) {
                    Suggestion suggestion = maybeSuggestion.get();

                    // If there is more than one token, it must be an exact match.
                    if (suggestion.name().equals(token)) {
                        String returnType = suggestion.typeText();
                        current = typeAndTrieMap.getOrDefault(returnType, UNKNOWN_TYPE_TRIE);
                    } else {
                        break;
                    }

                } else {
                    // Could not found a match to follow for the current token.
                    // Therefore no match is found.
                    break;
                }
            }
        }
        return autocompleteResults;
    }
}