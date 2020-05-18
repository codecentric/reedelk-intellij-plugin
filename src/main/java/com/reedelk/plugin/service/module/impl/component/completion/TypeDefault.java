package com.reedelk.plugin.service.module.impl.component.completion;

import com.reedelk.plugin.service.module.impl.component.metadata.TypeProxy;
import com.reedelk.runtime.api.commons.StringUtils;
import com.reedelk.runtime.api.flow.FlowContext;
import com.reedelk.runtime.api.message.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.reedelk.plugin.service.module.impl.component.completion.Suggestion.Type.PROPERTY;

public class TypeDefault {

    public static final String DEFAULT_RETURN_TYPE = Void.class.getSimpleName();
    public static final TypeProxy DEFAULT_RETURN_TYPE_PROXY = TypeProxy.create(Void.class);

    // The root of all the objects is 'Object'.
    public static final Trie OBJECT = new TrieImpl(null, null, null);

    // Default script signature is message and context.
    public static final Trie TRIE = new TrieImpl();
    static {
        Suggestion message = Suggestion.create(PROPERTY)
                .insertValue("message")
                .returnType(TypeProxy.create(Message.class))
                .returnTypeDisplayValue(Message.class.getSimpleName())
                .build();
        TRIE.insert(message);
        Suggestion context = Suggestion.create(PROPERTY)
                .insertValue("context")
                .returnType(TypeProxy.create(FlowContext.class))
                .returnTypeDisplayValue(FlowContext.class.getSimpleName())
                .build();
        TRIE.insert(context);
    }

    public static class Types {

        // TODO: Add lists, maps and so on ...
        public static void register(TypeAndTries allTypes, Map<String, Trie> trieMap) {
            // Lists
            Trie trie = new TrieImpl();
            trie.insert(Suggestion.create(Suggestion.Type.FUNCTION)
                    .insertValue("each { it }")
                    .lookupToken("each")
                    .tailText("{ it }")
                    .returnType(TypeProxy.create(Closure.class))
                    .returnTypeDisplayValue(StringUtils.EMPTY)
                    .cursorOffset(2)
                    .build());
            trie.insert(Suggestion.create(Suggestion.Type.FUNCTION)
                    .insertValue("eachWithIndex { it, i ->  }")
                    .tailText("{ it, i ->  }")
                    .lookupToken("eachWithIndex")
                    .returnType(TypeProxy.create(Closure.class))
                    .returnTypeDisplayValue(StringUtils.EMPTY)
                    .cursorOffset(2)
                    .build());
            trie.insert(Suggestion.create(Suggestion.Type.FUNCTION)
                    .insertValue("collect { it }")
                    .tailText("{ it }")
                    .lookupToken("collect")
                    .returnType(TypeProxy.create(Closure.class))
                    .returnTypeDisplayValue(StringUtils.EMPTY)
                    .cursorOffset(2)
                    .build());
            // TODO: Add to string
            trieMap.put(List.class.getName(), trie);

            Trie arrayList = new TrieImpl(List.class.getName(), null, null);
            trieMap.put(ArrayList.class.getName(), arrayList);

            Trie mapTrie = new TrieImpl();
            mapTrie.insert(Suggestion.create(Suggestion.Type.FUNCTION)
                    .insertValue("each { entry }")
                    .lookupToken("each")
                    .tailText("{ entry }")
                    .returnType(TypeProxy.create(Map.class))
                    .returnTypeDisplayValue(Map.class.getSimpleName())
                    .cursorOffset(2)
                    .build());
            // TODO: Add to string
            mapTrie.insert(Suggestion.create(Suggestion.Type.FUNCTION)
                    .insertValue("eachWithIndex { entry, i ->  }")
                    .tailText("{ entry, i ->  }")
                    .lookupToken("eachWithIndex")
                    .returnType(TypeProxy.create(Map.class))
                    .returnTypeDisplayValue(Map.class.getSimpleName())
                    .cursorOffset(2)
                    .build());
            // TODO: Add to string method

            trieMap.put(Map.class.getName(), mapTrie);

            Trie hashMap = new TrieImpl(Map.class.getName(), null, null);
            trieMap.put(HashMap.class.getName(), hashMap);

        }
    }
}
