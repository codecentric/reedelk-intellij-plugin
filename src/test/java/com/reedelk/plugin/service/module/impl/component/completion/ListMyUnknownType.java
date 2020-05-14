package com.reedelk.plugin.service.module.impl.component.completion;

import java.util.ArrayList;
import java.util.Map;

public class ListMyUnknownType extends ArrayList<MyUnknownType> implements TrieProvider {

    private ListMyUnknownType() {
    }

    public static void initialize(Map<String, Trie> trieMap) {
        new ListMyUnknownType().register(trieMap);
    }

    @Override
    public void register(Map<String, Trie> trieMap) {
        Trie trie = new TrieImpl(ArrayList.class.getName(), MyUnknownType.class.getName());
        trieMap.put(ListMyUnknownType.class.getName(), trie);
    }
}
