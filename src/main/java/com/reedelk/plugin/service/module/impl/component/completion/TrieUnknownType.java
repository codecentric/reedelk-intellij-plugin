package com.reedelk.plugin.service.module.impl.component.completion;

public class TrieUnknownType {

    private static final Trie UNKNOWN_TYPE_TRIE = new TrieDefault();

    public static Trie get() {
        return UNKNOWN_TYPE_TRIE;
    }
}
