package com.reedelk.plugin.service.module.impl.component.completion;

import com.reedelk.runtime.api.message.MessageAttributes;

import java.util.Map;

import static com.reedelk.plugin.service.module.impl.component.completion.SuggestionTestUtils.createProperty;

public class MessageAttributeType implements TrieProvider {

    private MessageAttributeType() {
    }

    public static void initialize(Map<String, Trie> trieMap) {
        new MessageAttributeType().register(trieMap);
    }

    @Override
    public void register(Map<String, Trie> trieMap) {
        Trie trie = new TrieDefault();
        trie.insert(createProperty("component", String.class.getName()));
        trieMap.put(MessageAttributes.class.getName(), trie);
    }
}
