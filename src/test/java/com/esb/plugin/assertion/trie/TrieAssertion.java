package com.esb.plugin.assertion.trie;

import com.esb.plugin.editor.properties.widget.input.script.Suggestion;
import com.esb.plugin.editor.properties.widget.input.script.trie.Trie;

import java.util.Set;

public class TrieAssertion {

    private final Trie trie;

    public TrieAssertion(Trie trie) {
        this.trie = trie;
    }

    public SearchByPrefixResultAssertion searchByPrefix(String prefix) {
        Set<Suggestion> suggestions = trie.searchByPrefix(prefix);
        return new SearchByPrefixResultAssertion(suggestions);
    }
}
