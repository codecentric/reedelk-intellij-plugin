package com.reedelk.plugin.service.module.impl.component.completion;

import java.util.Map;

public class MyItemType implements TrieProvider {

    private MyItemType() {
    }

    public static void initialize(Map<String, Trie> trieMap) {
        new MyItemType().register(trieMap);
    }

    @Override
    public void register(Map<String, Trie> trieMap) {
        Trie trie = new TrieImpl();
        trie.insert(SuggestionTestUtils.createFunction("method1", String.class.getName()));
        trieMap.put(MyItemType.class.getName(), trie);
    }
}
