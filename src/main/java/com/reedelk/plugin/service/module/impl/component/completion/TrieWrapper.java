package com.reedelk.plugin.service.module.impl.component.completion;

import java.util.ArrayList;
import java.util.Collection;

public class TrieWrapper implements Trie {

    private final Trie[] tries;

    public TrieWrapper(Trie ...tries) {
        this.tries = tries;
    }
    @Override
    public Collection<Suggestion> autocomplete(String word) {
        Collection<Suggestion> result = new ArrayList<>();
        for (Trie trie : tries) {
            result.addAll(trie.autocomplete(word));
        }
        return result;
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void insert(Suggestion suggestion) {
        throw new UnsupportedOperationException();
    }
}
