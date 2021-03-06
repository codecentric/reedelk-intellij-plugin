package de.codecentric.reedelk.plugin.service.module.impl.component.completion;

import org.jetbrains.annotations.NotNull;

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

    @NotNull
    public Trie getOrDefault(String fullyQualifiedTypeName) {
        for (Map<String, Trie> typeAndTrie : typeAndTries) {
            Trie typeInfo = typeAndTrie.get(fullyQualifiedTypeName);
            if (typeInfo != null) return typeInfo;
        }
        return new TrieDefault(fullyQualifiedTypeName);
    }
}
