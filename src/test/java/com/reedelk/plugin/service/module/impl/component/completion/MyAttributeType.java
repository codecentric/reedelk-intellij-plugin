package com.reedelk.plugin.service.module.impl.component.completion;

import com.reedelk.runtime.api.message.MessageAttributes;

import java.util.Map;

import static com.reedelk.plugin.service.module.impl.component.completion.SuggestionTestUtils.createProperty;

public class MyAttributeType extends MessageAttributes implements TrieProvider {

    private MyAttributeType() {
    }

    public static void initialize(Map<String, Trie> trieMap) {
        new MyAttributeType().register(trieMap);
    }

    @Override
    public void register(Map<String, Trie> trieMap) {
        Trie trie = new TrieImpl(MessageAttributes.class.getName(), null);
        trie.insert(createProperty("attributeProperty1", String.class.getName()));
        trie.insert(createProperty("attributeProperty2", long.class.getName()));
        trieMap.put(MyAttributeType.class.getName(), trie);
    }
}
