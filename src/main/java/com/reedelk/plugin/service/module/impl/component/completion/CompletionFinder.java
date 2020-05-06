package com.reedelk.plugin.service.module.impl.component.completion;

import com.reedelk.module.descriptor.model.component.ComponentOutputDescriptor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class CompletionFinder {

    private static final TypeInfo UNKNOWN_TYPE_TRIE = new TypeInfo();

    public static Collection<Suggestion> find(Trie root, TrieMapWrapper typeAndTrieMap, ComponentOutputDescriptor componentOutputDescriptor, String[] tokens) {
        Trie current = root;
        Collection<Suggestion> autocompleteResults = new ArrayList<>();
        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i];
            if (current == null) {
                autocompleteResults = new ArrayList<>();
            } else if (i == tokens.length - 1) {
                autocompleteResults = current.autocomplete(token);
            } else {
                Optional<Suggestion> maybeSuggestion = current.autocomplete(token).stream().findFirst();
                if (maybeSuggestion.isPresent()) {
                    Suggestion suggestion = maybeSuggestion.get();

                    // If there is more than one token, it must be an exact match.
                    if (suggestion.name().equals(token)) {
                        String returnType = suggestion.typeText(componentOutputDescriptor);
                        current = typeAndTrieMap.getOrDefault(returnType, UNKNOWN_TYPE_TRIE).getTrie();
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
