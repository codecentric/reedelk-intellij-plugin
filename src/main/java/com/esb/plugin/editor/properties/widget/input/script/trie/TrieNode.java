package com.esb.plugin.editor.properties.widget.input.script.trie;

import com.esb.plugin.editor.properties.widget.input.script.SuggestionType;

import java.util.HashMap;
import java.util.Map;

public class TrieNode {

    private final Map<Character, TrieNode> children = new HashMap<>();
    private SuggestionType type;
    private boolean endOfWord;

    Map<Character, TrieNode> getChildren() {
        return children;
    }

    boolean isEndOfWord() {
        return endOfWord;
    }

    void setEndOfWord(boolean endOfWord) {
        this.endOfWord = endOfWord;
    }

    public void setType(SuggestionType type) {
        this.type = type;
    }

    public SuggestionType getType() {
        return type;
    }
}
