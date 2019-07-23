package com.esb.plugin.editor.properties.widget.input.script.suggestion;

import java.util.HashMap;
import java.util.Map;

public class SuggestionTreeNode {

    private final Map<Character, SuggestionTreeNode> children = new HashMap<>();

    private SuggestionType type;
    private boolean endOfWord;

    Map<Character, SuggestionTreeNode> getChildren() {
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
