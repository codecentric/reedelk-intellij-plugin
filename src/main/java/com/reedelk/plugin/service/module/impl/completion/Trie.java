package com.reedelk.plugin.service.module.impl.completion;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;

public class Trie {

    private TrieNode root;

    public Trie() {
        this.root = new TrieNode();
    }

    public void insert(Suggestion suggestion) {
        TrieNode current = root;
        String word = suggestion.getToken();
        for (int i = 0; i < word.length(); i++) {
            current = current.getChildren().computeIfAbsent(word.charAt(i), c -> new TrieNode());
        }
        current.setSuggestion(suggestion);
    }

    public List<Suggestion> traversal(TrieNode start, List<Suggestion> suggestion, String current) {
        if (start.isSuggestion()) {
            suggestion.add(start.getSuggestion());
        }
        start.getChildren().forEach((character, trieNode) -> {
            String newCurrent = current + character;
            traversal(trieNode, suggestion, newCurrent);
        });
        return suggestion;
    }

    public List<Suggestion> autocomplete(String word) {
        List<Suggestion> suggestions = new ArrayList<>();
        TrieNode current = root;
        int i = 0;
        int lastWordIndex = -1;
        while (i < word.length()) {
            char c = word.charAt(i);
            Map<Character, TrieNode> children = current.getChildren();
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

        if (current.isSuggestion()) {
            return singletonList(current.getSuggestion());
        } else {
            String prefix = lastWordIndex != -1 ? word.substring(lastWordIndex + 1) : word;
            return traversal(current, suggestions, prefix);
        }
    }
}
