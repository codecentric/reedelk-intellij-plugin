package com.reedelk.plugin.service.module.impl.component.completion;

import com.reedelk.module.descriptor.model.component.ComponentOutputDescriptor;
import com.reedelk.plugin.service.module.impl.component.completion.commons.DynamicType;
import com.reedelk.plugin.service.module.impl.component.completion.commons.UnknownTypeTrie;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CompletionFinder {

    private final TrieMapWrapper typeAndTrieMap;

    public CompletionFinder(TrieMapWrapper typeAndTrieMap) {
        this.typeAndTrieMap = typeAndTrieMap;
    }

    public Collection<Suggestion> find(Trie root, ComponentOutputDescriptor componentOutputDescriptor, String[] tokens) {
        Trie current = root;
        Collection<Suggestion> autocompleteResults = new ArrayList<>();

        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i];

            if (current == null) {
                autocompleteResults = new ArrayList<>();

            } else if (i == tokens.length - 1) {
                autocompleteResults = autocomplete(current, token, componentOutputDescriptor);

            } else {
                Collection<Suggestion> suggestions = autocomplete(current, token, componentOutputDescriptor);
                List<Trie> exactMatchTries = new ArrayList<>();
                for (Suggestion suggestion : suggestions) {
                    // We only need to find exact matches. If there are no exact matches,
                    // we can not move forward with the autocomplete.
                    if (suggestion.name().equals(token)) {
                        Trie trie = typeAndTrieMap.getOrDefault(suggestion.typeText(), UnknownTypeTrie.get());
                        exactMatchTries.add(trie);
                    }
                }

                // We must stop if there are no exact matches.
                if (exactMatchTries.isEmpty()) break;

                // If there is at least one exact match, we can move forward to the
                // next token with the autocomplete.
                current = new TrieWrapper(exactMatchTries);
            }
        }

        return autocompleteResults;
    }

    private Collection<Suggestion> autocomplete(Trie current, String token, ComponentOutputDescriptor descriptor) {
        Collection<Suggestion> suggestions = current.autocomplete(token, typeAndTrieMap);
        List<Suggestion> withDynamicSuggestions = new ArrayList<>();
        for (Suggestion suggestion : suggestions) {
            if (DynamicType.is(suggestion)) {
                Collection<Suggestion> dynamicSuggestion =
                        DynamicType.createDynamicSuggestion(typeAndTrieMap, descriptor, suggestion);
                withDynamicSuggestions.addAll(dynamicSuggestion);
            } else {
                withDynamicSuggestions.add(suggestion);
            }
        }
        return withDynamicSuggestions;
    }
}
