package com.reedelk.plugin.service.application.impl.completion;

import java.util.HashMap;
import java.util.Map;

public class TrieNode {

    private Map<Character, TrieNode> children = new HashMap<>();
    private SuggestionType type;
    private String typeName;
    private boolean isWord;

    public TrieNode() {
    }

    public TrieNode(SuggestionType type, String typeName) {
        this.type = type;
        this.typeName = typeName;
        this.isWord = true;
    }

    public Map<Character, TrieNode> getChildren() {
        return children;
    }

    public boolean isEndOfWord() {
        return isWord;
    }

    public void setType(SuggestionType type) {
        this.type = type;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public void setEndOfWord(boolean endOfWord) {
        this.isWord = endOfWord;
    }

    public SuggestionType getType() {
        return type;
    }

    public String getTypeName() {
        return typeName;
    }
}