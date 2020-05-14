package com.reedelk.plugin.service.module.impl.component.completion;

import com.reedelk.runtime.api.flow.FlowContext;
import com.reedelk.runtime.api.message.Message;

import java.util.ArrayList;
import java.util.Map;

import static com.reedelk.plugin.service.module.impl.component.completion.Suggestion.Type.PROPERTY;

public class Default {

    public static final String DEFAULT_RETURN_TYPE = "void";

    public static final Trie UNKNOWN = new TrieImpl();

    // Default script signature is message and context.
    public static final Trie TRIE = new TrieImpl();
    static {
        Suggestion message = Suggestion.create(PROPERTY)
                .insertValue("message")
                .returnType(Message.class.getName())
                .returnTypeDisplayValue(PresentableTypeUtils.from(Message.class.getName()))
                .build();
        TRIE.insert(message);
        Suggestion context = Suggestion.create(PROPERTY)
                .insertValue("context")
                .returnType(FlowContext.class.getName())
                .returnTypeDisplayValue(PresentableTypeUtils.from(FlowContext.class.getName()))
                .build();
        TRIE.insert(context);
    }

    public static class Types {

        // TODO: Add lists, maps and so on ...
        public static void register(Map<String, Trie> trieMap) {
            // Init async
            Trie trie = new TrieImpl();
            trie.insert(Suggestion.create(Suggestion.Type.FUNCTION)
                    .insertValue("each { it }")
                    .lookupToken("each")
                    .cursorOffset(2)
                    .build());
            trie.insert(Suggestion.create(Suggestion.Type.FUNCTION)
                    .insertValue("eachWithIndex { it, i ->  }")
                    .lookupToken("eachWithIndex")
                    .cursorOffset(2)
                    .build());
            trie.insert(Suggestion.create(Suggestion.Type.FUNCTION)
                    .insertValue("collect { it }")
                    .lookupToken("collect")
                    .cursorOffset(2)
                    .build());
            trieMap.put(ArrayList.class.getName(), trie);
        }
    }
}
