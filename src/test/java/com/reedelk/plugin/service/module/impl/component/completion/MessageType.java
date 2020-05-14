package com.reedelk.plugin.service.module.impl.component.completion;

import com.reedelk.runtime.api.message.Message;
import com.reedelk.runtime.api.message.MessageAttributes;
import com.reedelk.runtime.api.message.MessagePayload;

import java.util.Map;

import static com.reedelk.plugin.service.module.impl.component.completion.SuggestionTestUtils.createFunction;

public class MessageType implements TrieProvider {

    private MessageType() {
    }

    public static void initialize(Map<String, Trie> trieMap) {
        new MessageType().register(trieMap);
    }

    @Override
    public void register(Map<String, Trie> trieMap) {
        Trie trie = new TrieDefault();
        trie.insert(createFunction("payload", MessagePayload.class.getName()));
        trie.insert(createFunction("attributes", MessageAttributes.class.getName()));
        trieMap.put(Message.class.getName(), trie);
    }
}
