package com.reedelk.plugin.service.module.impl.component.completion;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.groupingBy;

public class SuggestionTree {

    private static final TrieDefault UNKNOWN_TYPE_TRIE = new TrieDefault();

    private final TrieDefault root = new TrieDefault();
    private final Map<String, TrieDefault> typeTriesMap;

    public SuggestionTree(Map<String, TrieDefault> typeTriesMap) {
        this.typeTriesMap = typeTriesMap;
    }

    public Map<String, TrieDefault> getTypeTriesMap() {
        return typeTriesMap;
    }

    public List<Suggestion> autocomplete(String[] tokens) {
        TrieDefault current = root;
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
                    if (suggestion.lookupString().equals(token)) {
                        String returnType = suggestion.typeText();
                        current = typeTriesMap.getOrDefault(returnType, UNKNOWN_TYPE_TRIE);
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

    public void add(Suggestion suggestion) {
        root.insert(suggestion);
    }

    public void add(List<Suggestion> suggestions) {

        Map<String, List<Suggestion>> typeAwareGroupedByType =
                suggestions.stream().collect(groupingBy(Suggestion::typeText));

        typeAwareGroupedByType
                .keySet().forEach(type -> typeTriesMap.put(type, new TrieDefault()));

        typeAwareGroupedByType.forEach((type, descriptorsGroupedByType) -> {
            // Build trie with autocomplete definitions.
            TrieDefault current = typeTriesMap.get(type);

            // For each node
            descriptorsGroupedByType.forEach(typeAwareItem -> {
                if (typeAwareItem.lookupString().equals("global")) {//TODO: Finish me
                    // If it is global we add it to the root. The return type suggestion tree
                    // is the tree defining functions and variables belonging to this type.
                    root.insert(typeAwareItem);
                } else {
                    current.insert(typeAwareItem);
                }
            });
        });
    }
}
