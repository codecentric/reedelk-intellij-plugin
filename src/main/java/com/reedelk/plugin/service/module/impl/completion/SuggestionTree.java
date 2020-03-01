package com.reedelk.plugin.service.module.impl.completion;

import com.reedelk.plugin.commons.ArrayUtils;
import com.reedelk.runtime.api.commons.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.groupingBy;

public class SuggestionTree {

    private static final String[] EMPTY = new String[]{StringUtils.EMPTY};
    private static final Trie UNKNOWN_TYPE_TRIE = new Trie();

    private final Trie root = new Trie();
    private final Map<String, Trie> typeTriesMap;

    public SuggestionTree(Map<String, Trie> typeTriesMap) {
        this.typeTriesMap = typeTriesMap;
    }

    public Map<String, Trie> getTypeTriesMap() {
        return typeTriesMap;
    }

    public List<Suggestion> autocomplete(String input) {
        String[] tokens = InputTokenizer.tokenize(input);

        if (input.endsWith(".")) {
            tokens = ArrayUtils.concatenate(tokens, EMPTY);
        }

        Trie current = root;
        List<Suggestion> autocompleteResults = new ArrayList<>();
        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i];
            if (i == tokens.length - 1) {
                autocompleteResults = current.autocomplete(token);
            } else {
                Optional<Suggestion> first = current.autocomplete(token).stream().findFirst();
                if (first.isPresent()) {
                    Suggestion suggestion = first.get();
                    String returnType = suggestion.getReturnType();
                    current = typeTriesMap.getOrDefault(returnType, UNKNOWN_TYPE_TRIE);
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
                suggestions.stream().collect(groupingBy(Suggestion::getType));

        typeAwareGroupedByType
                .keySet().forEach(type -> typeTriesMap.put(type, new Trie()));

        typeAwareGroupedByType.forEach((type, descriptorsGroupedByType) -> {
            // Build trie with autocomplete definitions.
            Trie current = typeTriesMap.get(type);

            // For each node
            descriptorsGroupedByType.forEach(typeAwareItem -> {
                if (typeAwareItem.isGlobal()) {
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
