package com.reedelk.plugin.service.module.impl.component.completion;

import java.util.HashMap;
import java.util.Map;

import static com.reedelk.plugin.service.module.impl.component.completion.SuggestionTestUtils.createFunction;

public class GenericMapTypeFunctions implements TrieProvider {

    private GenericMapTypeFunctions() {
    }

    public static void initialize(Map<String, Trie> trieMap) {
        new GenericMapTypeFunctions().register(trieMap);
    }

    @Override
    public void register(Map<String, Trie> trieMap) {
        Trie trie = new TrieDefault(null, null);
        trie.insert(createFunction("size", int.class.getName()));
        trie.insert(createFunction("each", Void.class.getName()));
        trie.insert(createFunction("eachWithIndex", Void.class.getName()));
        trie.insert(createFunction("find", Object.class.getName()));
        trieMap.put(Map.class.getName(), trie);
        // Hashmap for objects extending from it.
        trieMap.put(HashMap.class.getName(), new TrieDefault(Map.class.getName(), null));
    }
}
