package com.reedelk.plugin.service.module.impl.completion;

import java.util.HashMap;
import java.util.Map;

public class TrieNode {

    private Suggestion suggestion;
    private Map<Character, TrieNode> children = new HashMap<>();

    public boolean isSuggestion() {
        return (children.isEmpty() && suggestion != null) ||
                (children.containsKey('.') && suggestion != null) ||
                suggestion != null;
    }

    public Map<Character, TrieNode> getChildren() {
        return children;
    }

    public Suggestion getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(Suggestion suggestion) {
        this.suggestion = suggestion;
    }
}
