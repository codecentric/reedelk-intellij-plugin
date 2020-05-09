package com.reedelk.plugin.service.module.impl.component.completion.commons;

import com.reedelk.plugin.service.module.impl.component.completion.Trie;
import com.reedelk.plugin.service.module.impl.component.completion.TrieDefault;

public class UnknownTypeTrie {

    private static final Trie UNKNOWN_TYPE_TRIE = new TrieDefault();

    public static Trie get() {
        return UNKNOWN_TYPE_TRIE;
    }
}
