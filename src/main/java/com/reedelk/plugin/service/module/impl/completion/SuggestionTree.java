package com.reedelk.plugin.service.module.impl.completion;

import com.reedelk.plugin.commons.ArrayUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.groupingBy;

public class SuggestionTree<T extends TypeAware> {

    private static final String[] EMPTY = new String[]{""};

    private final Trie<T> root = new Trie<>();
    private final Map<String, Trie<T>> typeTriesMap;

    public SuggestionTree(Map<String, Trie<T>> typeTriesMap) {
        this.typeTriesMap = typeTriesMap;
    }

    public Map<String, Trie<T>> getTypeTriesMap() {
        return typeTriesMap;
    }

    public List<TrieResult<T>> autocomplete(String input) {
        String[] tokens = InputTokenizer.tokenize(input);

        if (input.endsWith(".")) {
            tokens = ArrayUtils.concatenate(tokens, EMPTY);
        }

        Trie<T> current = root;
        List<TrieResult<T>> autocompleteResults = new ArrayList<>();
        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i];
            if (i == tokens.length - 1) {
                autocompleteResults = current.autocomplete(token);
            } else {
                Optional<TrieResult<T>> first = current.autocomplete(token).stream().findFirst();
                if (first.isPresent()) {
                    TrieResult<T> trieResult = first.get();
                    String returnType = trieResult.getTypeAware().getReturnType();
                    current = typeTriesMap.get(returnType);
                } else {
                    // Could not found a match to follow for the current token.
                    // Therefore no match is found.
                    break;
                }
            }
        }
        return autocompleteResults;
    }

    public void add(T typeAware) {
        root.insert(typeAware);
    }

    public void add(List<T> typeAware) {

        Map<String, List<T>> typeAwareGroupedByType =
                typeAware.stream().collect(groupingBy(TypeAware::getType));

        typeAwareGroupedByType
                .keySet().forEach(type -> typeTriesMap.put(type, new Trie<T>()));

        typeAwareGroupedByType.forEach((type, descriptorsGroupedByType) -> {
            // Build trie with autocomplete definitions.
            Trie<T> current = typeTriesMap.get(type);

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
