package de.codecentric.reedelk.plugin.service.module.impl.component.completion;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class TrieMultipleWrapper implements Trie {

    private final Collection<Trie> tries;

    public TrieMultipleWrapper(List<Trie> tries) {
        this.tries = tries;
    }

    public TrieMultipleWrapper(Trie ...tries) {
        this.tries = Arrays.asList(tries);
    }

    @Override
    public Collection<Suggestion> autocomplete(String word, TypeAndTries typeAndTrieMap) {
        Collection<Suggestion> result = new HashSet<>();
        for (Trie trie : tries) {
            result.addAll(trie.autocomplete(word, typeAndTrieMap));
        }
        return result;
    }

    @Override
    public String toSimpleName(TypeAndTries typeAndTries) {
        throw new UnsupportedOperationException();
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
