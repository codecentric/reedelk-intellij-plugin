package com.reedelk.plugin.service.module.impl.component.completion;

import com.reedelk.runtime.api.commons.StringUtils;

import java.util.*;

import static com.reedelk.runtime.api.commons.StringUtils.isNotBlank;

public class TrieDefault implements Trie {

    protected final String fullyQualifiedName;
    private final String extendsType;
    private final String displayName;

    private TrieNode root;

    public TrieDefault() {
        this.root = new TrieNode();
        this.displayName = null;
        this.extendsType = null;
        this.fullyQualifiedName = null;
    }

    public TrieDefault(String fullyQualifiedName, String extendsType, String displayName) {
        this.root = new TrieNode();
        this.fullyQualifiedName = fullyQualifiedName;
        this.displayName = displayName;
        this.extendsType = StringUtils.isBlank(extendsType) ? Object.class.getName() : extendsType;
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

        if (!Object.class.getName().equals(fullyQualifiedName)) {
            List<Suggestion> extendsSuggestions = addExtendsTypeSuggestions(this, typeAndTrieMap, word);
            autocomplete.addAll(extendsSuggestions);
        }
        return autocomplete;
    }

    @Override
    public String extendsType() {
        return extendsType;
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

    // All the extends type must contain the original
    private List<Suggestion> addExtendsTypeSuggestions(Trie currentTrie, TypeAndTries typeAndTrieMap, String token) {
        List<Suggestion> suggestions = new ArrayList<>();
        if (isNotBlank(currentTrie.extendsType())) {
            String extendsType = currentTrie.extendsType();
            Trie currentTypeTrie = typeAndTrieMap.getOrDefault(extendsType);
            Collection<Suggestion> autocomplete = currentTypeTrie.autocomplete(token, typeAndTrieMap);
            suggestions.addAll(autocomplete);
            List<Suggestion> suggestions1 = addExtendsTypeSuggestions(currentTypeTrie, typeAndTrieMap, token);
            suggestions.addAll(suggestions1);
        }
        return suggestions;
    }
}
