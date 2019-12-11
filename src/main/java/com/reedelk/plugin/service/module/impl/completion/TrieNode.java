package com.reedelk.plugin.service.module.impl.completion;

import java.util.HashMap;
import java.util.Map;

public class TrieNode {

    private Map<Character, TrieNode> children = new HashMap<>();
    private boolean isWord;
    private Suggestion suggestion;

    public TrieNode() {
    }

    public Map<Character, TrieNode> getChildren() {
        return children;
    }

    public boolean isEndOfWord() {
        return isWord;
    }

    public void setEndOfWord(boolean endOfWord) {
        this.isWord = endOfWord;
    }

    public Suggestion getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(Suggestion suggestion) {
        this.suggestion = suggestion;
    }
}