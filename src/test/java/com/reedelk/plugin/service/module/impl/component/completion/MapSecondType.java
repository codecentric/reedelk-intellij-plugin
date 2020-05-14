package com.reedelk.plugin.service.module.impl.component.completion;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static com.reedelk.plugin.service.module.impl.component.completion.SuggestionTestUtils.createProperty;

public class MapSecondType extends HashMap<String, Serializable> implements TrieProvider {

    private MapSecondType() {
    }

    public static void initialize(Map<String, Trie> trieMap) {
        new MapSecondType().register(trieMap);
    }

    @Override
    public void register(Map<String, Trie> trieMap) {
        Trie trie = new TrieImpl(HashMap.class.getName(), null);
        trie.insert(createProperty("secondProperty1", String.class.getName()));
        trie.insert(createProperty("secondProperty2", long.class.getName()));
        trieMap.put(MapSecondType.class.getName(), trie);
    }
}
