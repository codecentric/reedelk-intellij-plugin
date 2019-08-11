package com.reedelk.plugin.assertion.trie;

import com.reedelk.plugin.editor.properties.widget.input.script.suggestion.Suggestion;
import com.reedelk.plugin.editor.properties.widget.input.script.suggestion.SuggestionTree;

import java.util.Set;

public class TrieAssertion {

    private final SuggestionTree suggestionTree;

    public TrieAssertion(SuggestionTree suggestionTree) {
        this.suggestionTree = suggestionTree;
    }

    public SearchByPrefixResultAssertion searchByPrefix(String prefix) {
        Set<Suggestion> suggestions = suggestionTree.searchByPrefix(prefix);
        return new SearchByPrefixResultAssertion(suggestions);
    }
}
