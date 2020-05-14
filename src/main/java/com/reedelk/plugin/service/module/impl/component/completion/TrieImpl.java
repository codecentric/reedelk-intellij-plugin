package com.reedelk.plugin.service.module.impl.component.completion;

import com.reedelk.runtime.api.commons.StringUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TrieImpl implements Trie {

    private final String extendsType;
    private final String displayName;
    private final String listItemType;
    private TrieNode root;

    public TrieImpl() {
        this.root = new TrieNode();
        this.extendsType = null;
        this.listItemType = null;
        this.displayName = null;
    }

    public TrieImpl(String extendsType, String listItemType, String displayName) {
        this.root = new TrieNode();
        this.listItemType = listItemType;
        this.displayName = displayName;
        this.extendsType = extendsType;
    }

    @Override
    public void insert(Suggestion suggestion) {
        TrieNode current = root;
        String word = suggestion.getInsertValue();
        for (int i = 0; i < word.length(); i++) {
            current = current.getChildren().computeIfAbsent(word.charAt(i), c -> new TrieNode());
        }
        current.addSuggestion(suggestion);
    }

    @Override
    public Collection<Suggestion> autocomplete(String word, TypeAndTries typeAndTrieMap) {
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
            return new HashSet<>(current.getSuggestions());
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
    public String displayName() {
        return displayName;
    }

    @Override
    public void clear() {
        this.root = null;
        this.root = new TrieNode();
    }

    private Set<Suggestion> traversal(TrieNode start, Set<Suggestion> suggestions, String current) {
        if (start.isSuggestion()) {
            suggestions.addAll(start.getSuggestions());
        }
        start.getChildren().forEach((character, trieNode) -> {
            String newCurrent = current + character;
            traversal(trieNode, suggestions, newCurrent);
        });
        return suggestions;
    }

    private static void addExtendsTypeSuggestions(Trie current, TypeAndTries typeAndTrieMap, String token, Collection<Suggestion> suggestions) {
        if (current != null && StringUtils.isNotBlank(current.extendsType())) {
            String extendsType = current.extendsType();
            Trie currentTypeTrie = typeAndTrieMap.getOrDefault(extendsType, Default.UNKNOWN);
            suggestions.addAll(currentTypeTrie.autocomplete(token, typeAndTrieMap));
            addExtendsTypeSuggestions(currentTypeTrie, typeAndTrieMap, token, suggestions);
        }
    }
}
