package com.reedelk.plugin.service.module.impl.component.completion;

import java.util.ArrayList;
import java.util.Map;

public class ListMyItemType extends ArrayList<MyItemType> implements TrieProvider {

    private ListMyItemType() {
    }

    public static void initialize(Map<String, Trie> trieMap) {
        new ListMyItemType().register(trieMap);
    }

    @Override
    public void register(Map<String, Trie> trieMap) {
        Trie trie = new TrieDefault(ArrayList.class.getName(), MyItemType.class.getName());
        trieMap.put(ListMyItemType.class.getName(), trie);
    }
}
