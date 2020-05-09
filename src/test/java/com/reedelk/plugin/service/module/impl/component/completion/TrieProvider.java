package com.reedelk.plugin.service.module.impl.component.completion;

import java.util.Map;

public interface TrieProvider {
    void register(Map<String, Trie> trieMap);
}
