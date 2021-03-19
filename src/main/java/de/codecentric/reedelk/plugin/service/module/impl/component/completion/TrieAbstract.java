package de.codecentric.reedelk.plugin.service.module.impl.component.completion;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

abstract class TrieAbstract implements Trie {

    private TrieNode root;

    protected TrieAbstract() {
        root = new TrieNode();
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
    public Collection<Suggestion> autocomplete(String token, TypeAndTries typeAndTrieMap) {
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
            return completeUntilSuggestionFound(current, suggestions, token);
        }
    }

    @Override
    public void clear() {
        this.root = null;
        this.root = new TrieNode();
    }

    private Set<Suggestion> completeUntilSuggestionFound(TrieNode start, Set<Suggestion> suggestions, String current) {
        if (start.existAnySuggestion()) {
            // We add the suggestions from the current node to the list of suggestions
            // to return and then we continue with the children to discover other suggestions
            // matching the postfix below.
            suggestions.addAll(start.getSuggestions());
        }
        // We must continue to find suggestions until there are no more children in the trie.
        start.getChildren().forEach((character, trieNode) -> {
            String newCurrent = current + character;
            completeUntilSuggestionFound(trieNode, suggestions, newCurrent);
        });
        return suggestions;
    }
}
