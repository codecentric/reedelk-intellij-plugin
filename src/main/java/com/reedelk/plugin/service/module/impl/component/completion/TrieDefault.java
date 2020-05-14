package com.reedelk.plugin.service.module.impl.component.completion;

import com.reedelk.runtime.api.commons.StringUtils;

import java.util.*;

public class TrieDefault implements Trie {

    private final String extendsType;
    private final String listItemType;
    private TrieNode root;

    public TrieDefault() {
        this.root = new TrieNode();
        this.extendsType = null;
        this.listItemType = null;
    }

    public TrieDefault(String extendsType, String listItemType) {
        this.root = new TrieNode();
        this.listItemType = listItemType;
        this.extendsType = extendsType;
    }

    @Override
    public void insert(Suggestion suggestion) {
        TrieNode current = root;
        String word = suggestion.getLookup();
        for (int i = 0; i < word.length(); i++) {
            current = current.getChildren().computeIfAbsent(word.charAt(i), c -> new TrieNode());
        }
        current.setSuggestion(suggestion);
    }

    @Override
    public Collection<Suggestion> autocomplete(String word, TrieMapWrapper typeAndTrieMap) {
        Set<Suggestion> autocomplete = autocomplete(word);
        addExtendsTypeSuggestions(this, typeAndTrieMap, word, autocomplete);
        return autocomplete;
    }


    private Set<Suggestion> autocomplete(String token) {
        Set<Suggestion> suggestions = new HashSet<>();
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
            return Collections.singleton(current.getSuggestion());
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
    public String listItemType() {
        return listItemType;
    }

    @Override
    public void clear() {
        this.root = null;
        this.root = new TrieNode();
    }

    private Set<Suggestion> traversal(TrieNode start, Set<Suggestion> suggestion, String current) {
        if (start.isSuggestion()) {
            suggestion.add(start.getSuggestion());
        }
        start.getChildren().forEach((character, trieNode) -> {
            String newCurrent = current + character;
            traversal(trieNode, suggestion, newCurrent);
        });
        return suggestion;
    }

    private static void addExtendsTypeSuggestions(Trie current, TrieMapWrapper typeAndTrieMap, String token, Collection<Suggestion> suggestions) {
        if (current != null && StringUtils.isNotBlank(current.extendsType())) {
            String extendsType = current.extendsType();
            Trie currentTypeTrie = typeAndTrieMap.getOrDefault(extendsType, Default.UNKNOWN);
            suggestions.addAll(currentTypeTrie.autocomplete(token, typeAndTrieMap));
            addExtendsTypeSuggestions(currentTypeTrie, typeAndTrieMap, token, suggestions);
        }
    }
}
