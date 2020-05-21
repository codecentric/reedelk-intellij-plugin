package com.reedelk.plugin.service.module.impl.component.completion;

import com.reedelk.runtime.api.commons.StringUtils;
import com.reedelk.runtime.api.flow.FlowContext;
import com.reedelk.runtime.api.message.Message;
import com.reedelk.runtime.api.message.MessageAttributes;

import java.util.*;

import static com.reedelk.plugin.service.module.impl.component.completion.Suggestion.Type.*;
import static java.util.Arrays.asList;

public class TypeDefault {

    public static final String DEFAULT_PAYLOAD = Object.class.getName();
    public static final String DEFAULT_ATTRIBUTES = MessageAttributes.class.getName();

    public static final Collection<BuiltInType> BUILT_IN_TYPE =
            asList(new TypeObject(), new TypeList(), new TypeMap());

    // Default script signature is message and context.
    public static final Trie MESSAGE_AND_CONTEXT = new TrieRoot();
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

    public interface BuiltInType {
        void register(Map<String, Trie> typeTrieMap);
    }

    private static class TypeMap implements BuiltInType {
        @Override
        public void register(Map<String, Trie> typeTrieMap) {
            Trie mapTrie = new TrieDefault(Map.class.getName(), Object.class.getName(), Map.class.getSimpleName());
            typeTrieMap.put(Map.class.getName(), mapTrie);

            mapTrie.insert(Suggestion.create(CLOSURE)
                    .insertValue("each { entry }")
                    .lookupToken("each")
                    .tailText("{ entry }")
                    .returnType(TypeProxy.VOID)
                    .returnTypeDisplayValue(StringUtils.EMPTY)
                    .cursorOffset(2)
                    .build());

            mapTrie.insert(Suggestion.create(CLOSURE)
                    .insertValue("eachWithIndex { entry, i ->  }")
                    .tailText("{ entry, i ->  }")
                    .lookupToken("eachWithIndex")
                    .returnType(TypeProxy.VOID)
                    .returnTypeDisplayValue(StringUtils.EMPTY)
                    .cursorOffset(2)
                    .build());

            Trie hashMap = new TrieMap(HashMap.class.getName(), Object.class.getName(), Object.class.getName());
            typeTrieMap.put(HashMap.class.getName(), hashMap);
        }
    }

    private static class TypeList implements BuiltInType {
        @Override
        public void register(Map<String, Trie> typeTrieMap) {
            Trie trie = new TrieDefault(List.class.getName(), Object.class.getName(), List.class.getSimpleName());
            typeTrieMap.put(List.class.getName(), trie);

            trie.insert(Suggestion.create(CLOSURE)
                    .insertValue("each { it }")
                    .lookupToken("each")
                    .tailText("{ it }")
                    .returnType(TypeProxy.VOID)
                    .returnTypeDisplayValue(StringUtils.EMPTY)
                    .cursorOffset(2)
                    .build());

            trie.insert(Suggestion.create(CLOSURE)
                    .insertValue("eachWithIndex { it, i ->  }")
                    .tailText("{ it, i ->  }")
                    .lookupToken("eachWithIndex")
                    .returnType(TypeProxy.VOID)
                    .returnTypeDisplayValue(StringUtils.EMPTY)
                    .cursorOffset(2)
                    .build());

            trie.insert(Suggestion.create(CLOSURE)
                    .insertValue("collect { it }")
                    .tailText("{ it }")
                    .lookupToken("collect")
                    .returnType(TypeProxy.VOID)
                    .returnTypeDisplayValue(StringUtils.EMPTY)
                    .cursorOffset(2)
                    .build());

            Trie arrayList = new TrieList(ArrayList.class.getName(), Object.class.getName());
            typeTrieMap.put(ArrayList.class.getName(), arrayList);
        }
    }

    private static class TypeObject implements BuiltInType {

        @Override
        public void register(Map<String, Trie> typeTrieMap) {
            Trie objectTrie = new TrieDefault(Object.class.getName(), null, Object.class.getSimpleName());
            typeTrieMap.put(Object.class.getName(), objectTrie);

            TypeProxy stringType = TypeProxy.create(String.class.getName());
            objectTrie.insert(Suggestion.create(FUNCTION)
                    .lookupToken("toString")
                    .insertValue("toString()")
                    .tailText("()")
                    .returnType(stringType)
                    .returnTypeDisplayValue(String.class.getSimpleName())
                    .build());
        }
    }
}
