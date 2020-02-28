package com.reedelk.plugin.service.module.impl.completion;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;

public class Trie<T extends TypeAware> {

    private TrieNode<T> root;

    public Trie() {
        this.root = new TrieNode<>();
    }

    public void insert(T typeAware) {
        TrieNode<T> current = root;
        String word = typeAware.getToken();
        for (int i = 0; i < word.length(); i++) {
            current = current.getChildren().computeIfAbsent(word.charAt(i), c -> new TrieNode<>());
        }
        current.setTypeAware(typeAware);
    }

    public List<TrieResult<T>> traversal(TrieNode<T> start, List<TrieResult<T>> suggestion, String current) {
        if (start.isWord()) {
            TrieResult<T> result = new TrieResult<>(current, start.getTypeAware());
            suggestion.add(result);
        }
        start.getChildren().forEach((character, trieNode) -> {
            String newCurrent = current + character;
            traversal(trieNode, suggestion, newCurrent);
        });
        return suggestion;
    }

    public List<TrieResult<T>> autocomplete(String word) {
        List<TrieResult<T>> suggestions = new ArrayList<>();
        TrieNode<T> current = root;
        int i = 0;
        int lastWordIndex = -1;
        while (i < word.length()) {
            char c = word.charAt(i);
            Map<Character, TrieNode<T>> children = current.getChildren();
            if (children.containsKey(c)) {
                if (c == '.') {
                    lastWordIndex = i;
                }
                current = children.get(c);
            } else {
                return suggestions;
            }
            i++;
        }

        if (current.isWord()) {
            return singletonList(new TrieResult<>(word, current.getTypeAware()));
        } else {
            String prefix = lastWordIndex != -1 ? word.substring(lastWordIndex + 1) : word;
            return traversal(current, suggestions, prefix);
        }
    }
}
