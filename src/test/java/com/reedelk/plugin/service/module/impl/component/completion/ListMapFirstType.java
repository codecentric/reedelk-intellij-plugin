package com.reedelk.plugin.service.module.impl.component.completion;

import java.util.ArrayList;
import java.util.Map;

public class ListMapFirstType extends ArrayList<MapFirstType> implements TrieProvider {

    private ListMapFirstType() {
    }

    public static void initialize(Map<String, Trie> trieMap) {
        new ListMapFirstType().register(trieMap);
    }

    @Override
    public void register(Map<String, Trie> trieMap) {
        Trie trie = new TrieImpl(ArrayList.class.getName(), MapFirstType.class.getName());
        trieMap.put(ListMapFirstType.class.getName(), trie);
    }
}
