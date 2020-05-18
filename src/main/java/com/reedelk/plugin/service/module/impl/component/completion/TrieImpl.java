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
    private final String fullyQualifiedTypeName;
    private TrieNode root;

    public TrieImpl() {
        this.root = new TrieNode();
        this.extendsType = null;
        this.listItemType = null;
        this.displayName = null;
        this.fullyQualifiedTypeName = null;
    }

    public TrieImpl(String fullyQualifiedTypeName, String extendsType, String listItemType, String displayName) {
        this.root = new TrieNode();
        this.displayName = displayName;
        this.extendsType = extendsType;
        this.listItemType = listItemType;
        this.fullyQualifiedTypeName = fullyQualifiedTypeName;
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

    private Set<Suggestion> autocomplete(String token) {
        Set<Suggestion> suggestions = new HashSet<>();
        TrieNode current = root;
        int i = 0;
        while (i < token.length()) {
            char c = token.charAt(i);
            Map<Character, TrieNode> children = current.getChildren();
            if (children.containsKey(c)) {
                current = children.get(c);
            } else {
                return suggestions;
            }
            i++;
        }

        if (current.existAnySuggestion()) {
            return new HashSet<>(current.getSuggestions());
        } else {
            // We need to complete the suggestion from the current token
            // string until some suggestions are found.
            return traversal(current, suggestions, token);
        }
    }

    private Set<Suggestion> traversal(TrieNode start, Set<Suggestion> suggestions, String current) {
        if (start.existAnySuggestion()) {
            // We add the suggestions from the current node to the list of suggestions
            // to return and then we continue with the children to discover other suggestions
            // matching the postfix below.
            suggestions.addAll(start.getSuggestions());
        }
        // We must continue to find suggestions until there are no more children in the trie.
        start.getChildren().forEach((character, trieNode) -> {
            String newCurrent = current + character;
            traversal(trieNode, suggestions, newCurrent);
        });
        return suggestions;
    }

    private static void addExtendsTypeSuggestions(Trie current, TypeAndTries typeAndTrieMap, String token, Collection<Suggestion> suggestions) {
        // TODO: Check on default object, otherwise we would go on stackoverflow
        if (current != Default.OBJECT && current != null && StringUtils.isNotBlank(current.extendsType())) {
            String extendsType = current.extendsType();
            Trie currentTypeTrie = typeAndTrieMap.getOrDefault(extendsType);
            Collection<Suggestion> autocomplete = currentTypeTrie.autocomplete(token, typeAndTrieMap);
            // TODO: These autocomplete for the supertype must have the same type of the child.
            suggestions.addAll(autocomplete);
            addExtendsTypeSuggestions(currentTypeTrie, typeAndTrieMap, token, suggestions);
        }
    }
}
