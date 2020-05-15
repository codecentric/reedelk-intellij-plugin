package com.reedelk.plugin.service.module.impl.component.completion;

import java.util.*;

public class TrieNode {

    private Set<Suggestion> suggestions = new HashSet<>();
    private Map<Character, TrieNode> children = new HashMap<>();

    public boolean existAnySuggestion() {
        return !suggestions.isEmpty();
    }

    public Map<Character, TrieNode> getChildren() {
        return children;
    }

    public Set<Suggestion> getSuggestions() {
        return Collections.unmodifiableSet(suggestions);
    }

    public void addSuggestion(Suggestion suggestion) {
        this.suggestions.add(suggestion);
    }
}
