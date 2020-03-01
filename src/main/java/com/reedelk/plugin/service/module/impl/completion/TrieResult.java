package com.reedelk.plugin.service.module.impl.completion;

public class TrieResult {

    private String word;
    private Suggestion suggestion;

    TrieResult(String word, Suggestion suggestion) {
        this.word = word;
        this.suggestion = suggestion;
    }

    public String getWord() {
        return word;
    }

    public Suggestion getSuggestion() {
        return suggestion;
    }
}
