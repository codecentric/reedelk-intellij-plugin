package com.reedelk.plugin.service.module.impl.component.completion;

import com.reedelk.module.descriptor.model.component.ComponentOutputDescriptor;
import com.reedelk.plugin.exception.PluginException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.reedelk.runtime.api.commons.StringUtils.EMPTY;
import static java.util.stream.Collectors.toList;

public class CompletionFinder {

    private final TrieMapWrapper typeAndTrieMap;

    public CompletionFinder(TrieMapWrapper typeAndTrieMap) {
        this.typeAndTrieMap = typeAndTrieMap;
    }

    public Collection<Suggestion> find(Trie root, ComponentOutputDescriptor componentOutputDescriptor) {
        return find(root, new String[]{EMPTY}, componentOutputDescriptor);
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
                        Trie trie = typeAndTrieMap.getOrDefault(suggestion.typeText(), Default.UNKNOWN);
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
                createDynamicSuggestions(suggestion, descriptor, withDynamicSuggestions, flatten);
            } else {
                withDynamicSuggestions.add(suggestion);
            }
        }
        return withDynamicSuggestions;
    }

    private void createDynamicSuggestions(Suggestion originalSuggestion,
                                          ComponentOutputDescriptor descriptor,
                                          List<Suggestion> suggestionList,
                                          boolean flatten) {
        Collection<Suggestion> dynamicSuggestions =
                DynamicTypeUtils.createDynamicSuggestion(typeAndTrieMap, descriptor, originalSuggestion);
        if (flatten && dynamicSuggestions.size() > 1) {
            // If the suggestion is terminal, e.g. message.payload() we must flatten the dynamic suggestions into one.
            // the type of each separate suggestion is separated by a comma. This happens when a component might
            // have multiple possible output types (e.g RESTListener: byte[], String or Map, depending on the HTTP request mime type).
            Suggestion flattedSuggestions = flatten(dynamicSuggestions);
            suggestionList.add(flattedSuggestions);
        } else {
            // If the suggestion is not terminal we add all the possible output of the previous component.
            suggestionList.addAll(dynamicSuggestions);
        }
    }

    /**
     * Takes a group of suggestions which refer to the same lookup string and collapses them into one
     * single suggestion with type equal to a comma separated list of all the suggestions in the group.
     */
    private Suggestion flatten(Collection<Suggestion> suggestions) {
        Suggestion suggestion = suggestions.stream()
                .findAny()
                .orElseThrow(() -> new PluginException("Expected at least one dynamic suggestion."));
        List<String> possibleTypes = suggestions.stream()
                .map(s -> PresentableTypeUtils.from(s.typeText()))
                .collect(toList());
        return Suggestion.create(suggestion.getType())
                .withLookupString(suggestion.lookupString())
                .withPresentableText(suggestion.presentableText())
                .withName(suggestion.name())
                .withPresentableType(String.join(",", possibleTypes))
                .build();
    }
}
