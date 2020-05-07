package com.reedelk.plugin.service.module.impl.component.completion;

import java.util.Map;

public class TrieMapWrapper  {

    private final Map<String, Trie>[] typeAndTries;

    @SafeVarargs
    public TrieMapWrapper(Map<String, Trie> ...typeAndTries) {
        this.typeAndTries = typeAndTries;
    }

    public Trie getOrDefault(String fullyQualifiedTypeName, Trie defaultOne) {
        for (Map<String, Trie> typeAndTrie : typeAndTries) {
            Trie typeInfo = typeAndTrie.get(fullyQualifiedTypeName);
            if (typeInfo != null) return typeInfo;
        }
        return defaultOne;
    }
}
