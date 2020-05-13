package com.reedelk.plugin.service.module.impl.component.completion;

import com.reedelk.runtime.api.flow.FlowContext;
import com.reedelk.runtime.api.message.Message;

import java.util.ArrayList;
import java.util.Map;

import static com.reedelk.plugin.service.module.impl.component.completion.Suggestion.Type.PROPERTY;

public class Default {

    public static final Trie UNKNOWN = new TrieDefault();

    // Default script signature is message and context.
    public static final Trie TRIE = new TrieDefault();
    static {
        Suggestion message = Suggestion.create(PROPERTY)
                .withLookupString("message")
                .withType(Message.class.getName())
                .build();
        TRIE.insert(message);
        Suggestion context = Suggestion.create(PROPERTY)
                .withLookupString("context")
                .withType(FlowContext.class.getName())
                .build();
        TRIE.insert(context);
    }

    public static class Types {

        // TODO: Add lists, maps and so on ...
        public static void register(Map<String, Trie> trieMap) {
            // Init async
            Trie trie = new TrieDefault();
            trie.insert(Suggestion.create(Suggestion.Type.FUNCTION)
                    .withLookupString("each { it }")
                    .withPresentableText("each")
                    .withCursorOffset(2)
                    .build());
            trie.insert(Suggestion.create(Suggestion.Type.FUNCTION)
                    .withLookupString("eachWithIndex { it, i ->  }")
                    .withPresentableText("eachWithIndex")
                    .withCursorOffset(2)
                    .build());
            trie.insert(Suggestion.create(Suggestion.Type.FUNCTION)
                    .withLookupString("collect { it }")
                    .withPresentableText("collect")
                    .withCursorOffset(2)
                    .build());
            trieMap.put(ArrayList.class.getName(), trie);
        }
    }
}
