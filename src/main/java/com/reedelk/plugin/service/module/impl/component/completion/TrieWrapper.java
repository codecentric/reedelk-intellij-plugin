package com.reedelk.plugin.service.module.impl.component.completion;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class TrieWrapper implements Trie {

    private final Trie[] tries;

    public TrieWrapper(List<Trie> tries) {
        this.tries = tries.toArray(new Trie[]{});
    }

    public TrieWrapper(Trie ...tries) {
        this.tries = tries;
    }

    @Override
    public Collection<Suggestion> autocomplete(String word, TrieMapWrapper typeAndTrieMap) {
        Collection<Suggestion> result = new HashSet<>();
        for (Trie trie : tries) {
            result.addAll(trie.autocomplete(word, typeAndTrieMap));
        }
        return result;
    }

    @Override
    public String extendsType() {
        return null;
    }

    @Override
    public String listItemType() {
        return null;
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
