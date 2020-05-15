package com.reedelk.plugin.service.module.impl.component.completion;

import com.reedelk.runtime.api.flow.FlowContext;
import com.reedelk.runtime.api.message.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.reedelk.plugin.service.module.impl.component.completion.Suggestion.Type.PROPERTY;
import static com.reedelk.plugin.service.module.impl.component.completion.TypeUtils.typeDisplayValueOf;

public class Default {

    public static final String DEFAULT_RETURN_TYPE = "void";

    public static final Trie UNKNOWN = new TrieImpl();

    // Default script signature is message and context.
    public static final Trie TRIE = new TrieImpl();
    static {
        Suggestion message = Suggestion.create(PROPERTY)
                .insertValue("message")
                .returnType(Message.class.getName())
                .returnTypeDisplayValue(TypeUtils.toSimpleName(Message.class.getName()))
                .build();
        TRIE.insert(message);
        Suggestion context = Suggestion.create(PROPERTY)
                .insertValue("context")
                .returnType(FlowContext.class.getName())
                .returnTypeDisplayValue(TypeUtils.toSimpleName(FlowContext.class.getName()))
                .build();
        TRIE.insert(context);
    }

    public static class Types {

        // TODO: Add lists, maps and so on ...
        public static void register(TypeAndTries allTypes, Map<String, Trie> trieMap) {
            // Init async
            Trie trie = new TrieImpl();
            trie.insert(Suggestion.create(Suggestion.Type.FUNCTION)
                    .insertValue("each { it }")
                    .lookupToken("each")
                    .tailText("{ it }")
                    .returnType(List.class.getName())
                    .returnTypeDisplayValue(typeDisplayValueOf(allTypes, List.class.getName()))
                    .cursorOffset(2)
                    .build());
            trie.insert(Suggestion.create(Suggestion.Type.FUNCTION)
                    .insertValue("eachWithIndex { it, i ->  }")
                    .tailText("{ it, i ->  }")
                    .lookupToken("eachWithIndex")
                    .returnType(List.class.getName())
                    .returnTypeDisplayValue(typeDisplayValueOf(allTypes, List.class.getName()))
                    .cursorOffset(2)
                    .build());
            trie.insert(Suggestion.create(Suggestion.Type.FUNCTION)
                    .insertValue("collect { it }")
                    .tailText("{ it }")
                    .lookupToken("collect")
                    .returnType(List.class.getName())

                    .returnTypeDisplayValue(typeDisplayValueOf(allTypes, List.class.getName()))
                    .cursorOffset(2)
                    .build());
            trieMap.put(List.class.getName(), trie);

            Trie arrayList = new TrieImpl(List.class.getName(), null, null);
            trieMap.put(ArrayList.class.getName(), arrayList);
        }
    }
}
