package com.reedelk.plugin.service.module.impl.component.completion;

import com.reedelk.runtime.api.commons.StringUtils;
import com.reedelk.runtime.api.flow.FlowContext;
import com.reedelk.runtime.api.message.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.reedelk.plugin.service.module.impl.component.completion.Suggestion.Type.FUNCTION;
import static com.reedelk.plugin.service.module.impl.component.completion.Suggestion.Type.PROPERTY;

public class TypeDefault {

    public static final String DEFAULT_RETURN_TYPE = Void.class.getSimpleName();
    public static final TypeProxy DEFAULT_RETURN_TYPE_PROXY = TypeProxy.create(Void.class);

    // TODO: All the type must extend from this if they don't explicitly extend from anything,
    //  also the OBJECT must add the toString() method as well.
    // The root of all the objects is 'Object'.
    public static final Trie OBJECT = new TrieDefault();

    // Default script signature is message and context.
    public static final Trie MESSAGE_AND_CONTEXT = new TrieDefault();
    static {
        Suggestion message = Suggestion.create(PROPERTY)
                .insertValue("message")
                .returnType(TypeProxy.create(Message.class))
                .returnTypeDisplayValue(Message.class.getSimpleName())
                .build();
        MESSAGE_AND_CONTEXT.insert(message);
        Suggestion context = Suggestion.create(PROPERTY)
                .insertValue("context")
                .returnType(TypeProxy.create(FlowContext.class))
                .returnTypeDisplayValue(FlowContext.class.getSimpleName())
                .build();
        MESSAGE_AND_CONTEXT.insert(context);
    }

    public static class Types {

        // TODO: Add lists, maps and so on ...
        public static void register(TypeAndTries allTypes, Map<String, Trie> trieMap) {
            // Lists
            Trie trie = new TrieDefault();
            trie.insert(Suggestion.create(FUNCTION)
                    .insertValue("each { it }")
                    .lookupToken("each")
                    .tailText("{ it }")
                    .returnType(TypeProxy.create(TypeListClosure.class))
                    .returnTypeDisplayValue(StringUtils.EMPTY)
                    .cursorOffset(2)
                    .build());
            trie.insert(Suggestion.create(FUNCTION)
                    .insertValue("eachWithIndex { it, i ->  }")
                    .tailText("{ it, i ->  }")
                    .lookupToken("eachWithIndex")
                    .returnType(TypeProxy.create(TypeListClosure.class))
                    .returnTypeDisplayValue(StringUtils.EMPTY)
                    .cursorOffset(2)
                    .build());
            trie.insert(Suggestion.create(FUNCTION)
                    .insertValue("collect { it }")
                    .tailText("{ it }")
                    .lookupToken("collect")
                    .returnType(TypeProxy.create(TypeListClosure.class))
                    .returnTypeDisplayValue(StringUtils.EMPTY)
                    .cursorOffset(2)
                    .build());
            // TODO: Add to string (to string should be inherited from object?)
            trieMap.put(List.class.getName(), trie);

            Trie arrayList = new TrieList(ArrayList.class.getName(), List.class.getName(), List.class.getSimpleName(), Object.class.getName());
            trieMap.put(ArrayList.class.getName(), arrayList);

            Trie mapTrie = new TrieDefault();
            mapTrie.insert(Suggestion.create(FUNCTION)
                    .insertValue("each { entry }")
                    .lookupToken("each")
                    .tailText("{ entry }")
                    .returnType(TypeProxy.create(TypeMapClosure.class))
                    .returnTypeDisplayValue(StringUtils.EMPTY)
                    .cursorOffset(2)
                    .build());
            // TODO: Add to string (to string should be inherited from object?)
            mapTrie.insert(Suggestion.create(FUNCTION)
                    .insertValue("eachWithIndex { entry, i ->  }")
                    .tailText("{ entry, i ->  }")
                    .lookupToken("eachWithIndex")
                    .returnType(TypeProxy.create(TypeMapClosure.class))
                    .returnTypeDisplayValue(StringUtils.EMPTY)
                    .cursorOffset(2)
                    .build());
            // TODO: Add to string method

            trieMap.put(Map.class.getName(), mapTrie);

            Trie hashMap = new TrieMap(HashMap.class.getName(), Map.class.getName(), Map.class.getSimpleName(), Object.class.getName(), Object.class.getName());
            trieMap.put(HashMap.class.getName(), hashMap);

        }
    }
}
