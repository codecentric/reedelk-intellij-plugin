package com.reedelk.plugin.service.module.impl.completion;

import java.util.HashMap;
import java.util.Map;

public class TrieNode<T extends TypeAware> {

    private T typeAware;
    private Map<Character, TrieNode<T>> children = new HashMap<>();

    public boolean isWord() {
        return typeAware != null &&
                (children.isEmpty() || children.containsKey('.'));
    }

    public Map<Character, TrieNode<T>> getChildren() {
        return children;
    }

    public T getTypeAware() {
        return typeAware;
    }

    public void setTypeAware(T typeAware) {
        this.typeAware = typeAware;
    }
}
