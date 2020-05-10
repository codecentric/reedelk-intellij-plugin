package com.reedelk.plugin.service.module.impl.component.completion;

import com.reedelk.module.descriptor.model.component.ComponentOutputDescriptor;
import com.reedelk.plugin.exception.PluginException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class CompletionFinder {

    private final TrieMapWrapper typeAndTrieMap;

    public CompletionFinder(TrieMapWrapper typeAndTrieMap) {
        this.typeAndTrieMap = typeAndTrieMap;
    }

    public Collection<Suggestion> find(Trie root, String[] tokens, ComponentOutputDescriptor componentOutputDescriptor) {
        Trie current = root;
        Collection<Suggestion> autocompleteResults = new ArrayList<>();

        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i];

            if (current == null) {
                autocompleteResults = new ArrayList<>();

            } else if (i == tokens.length - 1) {
                autocompleteResults = autocomplete(current, token, componentOutputDescriptor, true);

            } else {
                Collection<Suggestion> suggestions = autocomplete(current, token, componentOutputDescriptor, false);
                List<Trie> exactMatchTries = new ArrayList<>();
                for (Suggestion suggestion : suggestions) {
                    // We only need to find exact matches. If there are no exact matches,
                    // we can not move forward with the autocomplete.
                    if (suggestion.name().equals(token)) {
                        Trie trie = typeAndTrieMap.getOrDefault(suggestion.typeText(), TrieUnknownType.get());
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

    private Collection<Suggestion> autocomplete(Trie current, String token, ComponentOutputDescriptor descriptor, boolean flatten) {
        Collection<Suggestion> suggestions = current.autocomplete(token, typeAndTrieMap);
        List<Suggestion> withDynamicSuggestions = new ArrayList<>();
        for (Suggestion suggestion : suggestions) {
            if (DynamicTypeUtils.is(suggestion)) {
                createDynamicSuggestions(descriptor, flatten, withDynamicSuggestions, suggestion);
            } else {
                withDynamicSuggestions.add(suggestion);
            }
        }
        return withDynamicSuggestions;
    }

    private void createDynamicSuggestions(ComponentOutputDescriptor descriptor, boolean flatten, List<Suggestion> withDynamicSuggestions, Suggestion suggestion) {
        Collection<Suggestion> dynamicSuggestions =
                DynamicTypeUtils.createDynamicSuggestion(typeAndTrieMap, descriptor, suggestion);
        if (flatten && dynamicSuggestions.size() > 1) {
            // If the suggestion is terminal, e.g. message.payload() we must flatten the dynamic suggestions into one.
            // the type of each separate suggestion is separated by a comma. This happens when a component might
            // have multiple possible output types (e.g RESTListener: byte[], String or Map, depending on the HTTP request mime type).
            withDynamicSuggestions.add(flatten(dynamicSuggestions));
        } else {
            // If the suggestion is not terminal we add all the possible output of the previous component.
            withDynamicSuggestions.addAll(dynamicSuggestions);
        }
    }

    private Suggestion flatten(Collection<Suggestion> dynamicSuggestions) {
        List<String> possibleTypes = dynamicSuggestions.stream()
                .map(suggestion -> PresentableTypeUtils.from(suggestion.typeText()))
                .collect(toList());
        Suggestion suggestion = dynamicSuggestions.stream()
                .findAny()
                .orElseThrow(() -> new PluginException("Expected at least one dynamic suggestion."));
        return Suggestion.create(suggestion.getType())
                .withLookupString(suggestion.lookupString())
                .withPresentableText(suggestion.presentableText())
                .withName(suggestion.name())
                .withPresentableType(String.join(",", possibleTypes))
                .build();
    }
}
