package com.reedelk.plugin.service.module.impl.component.completion;

import java.util.Map;

public class TypeAndTries {

    private final Map<String, Trie>[] typeAndTries;

    @SafeVarargs
    public TypeAndTries(Map<String, Trie> ...typeAndTries) {
        this.typeAndTries = typeAndTries;
    }

    public boolean contains(String fullyQualifiedTypeName) {
        for (Map<String, Trie> typeAndTrie : typeAndTries) {
            if (typeAndTrie.containsKey(fullyQualifiedTypeName)) {
                return true;
            }
        }
        return false;
    }

    // TODO :It is pointless to keep asking for default one because it is always the same UNKNOWN trie!!
    public Trie getOrDefault(String fullyQualifiedTypeName, Trie defaultOne) {
        for (Map<String, Trie> typeAndTrie : typeAndTries) {
            Trie typeInfo = typeAndTrie.get(fullyQualifiedTypeName);
            if (typeInfo != null) return typeInfo;
        }
        return defaultOne;
    }
}
