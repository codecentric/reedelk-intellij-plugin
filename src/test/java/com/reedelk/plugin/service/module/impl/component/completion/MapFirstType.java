package com.reedelk.plugin.service.module.impl.component.completion;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static com.reedelk.plugin.service.module.impl.component.completion.SuggestionTestUtils.createProperty;

public class MapFirstType extends HashMap<String, Serializable> implements TrieProvider {

    private MapFirstType() {
    }

    public static void initialize(Map<String, Trie> trieMap) {
        new MapFirstType().register(trieMap);
    }

    @Override
    public void register(Map<String, Trie> trieMap) {
        Trie trie = new TrieImpl(HashMap.class.getName(), null);
        trie.insert(createProperty("firstProperty1", String.class.getName()));
        trie.insert(createProperty("firstProperty2", String.class.getName()));
        trieMap.put(MapFirstType.class.getName(), trie);
    }
}
