package com.reedelk.plugin.service.module.impl.component.completion;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;

public class TrieDefault implements Trie {

    private final String extendsType;
    private TrieNode root;

    public TrieDefault() {
        this.root = new TrieNode();
        this.extendsType = null;
    }

    public TrieDefault(String extendsType) {
        this.root = new TrieNode();
        this.extendsType = extendsType;
    }

    @Override
    public void insert(Suggestion suggestion) {
        TrieNode current = root;
        String word = suggestion.lookupString();
        for (int i = 0; i < word.length(); i++) {
            current = current.getChildren().computeIfAbsent(word.charAt(i), c -> new TrieNode());
        }
        current.setSuggestion(suggestion);
    }

    @Override
    public List<Suggestion> autocomplete(String token) {
        List<Suggestion> suggestions = new ArrayList<>();
        TrieNode current = root;
        int i = 0;
        int lastWordIndex = -1;
        while (i < token.length()) {
            char c = token.charAt(i);
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
            String prefix = lastWordIndex != -1 ? token.substring(lastWordIndex + 1) : token;
            return traversal(current, suggestions, prefix);
        }
    }

    @Override
    public String extendsType() {
        return extendsType;
    }

    @Override
    public void clear() {
        this.root = null;
        this.root = new TrieNode();
    }

    private List<Suggestion> traversal(TrieNode start, List<Suggestion> suggestion, String current) {
        if (start.isSuggestion()) {
            suggestion.add(start.getSuggestion());
        }
        start.getChildren().forEach((character, trieNode) -> {
            String newCurrent = current + character;
            traversal(trieNode, suggestion, newCurrent);
        });
        return suggestion;
    }
}
