package com.reedelk.plugin.service.module.impl.component.completion;

import com.reedelk.runtime.api.commons.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.reedelk.plugin.service.module.impl.component.completion.Suggestion.Type.*;
import static com.reedelk.plugin.service.module.impl.component.completion.TypeBuiltInPrimitive.*;

class TypeBuiltInList implements TypeBuiltIn.BuiltInType {

    @Override
    public void register(Map<String, Trie> typeTrieMap, TypeAndTries typeAndTries) {
        Trie listTrie =
                new TrieList(List.class.getName(), Object.class.getName(), null, Object.class.getName());
        typeTrieMap.put(List.class.getName(), listTrie);

        listTrie.insert(Suggestion.create(CLOSURE)
                .insertValue("each { it }")
                .lookupToken("each")
                .tailText("{ it }")
                .returnType(TypeProxy.create(ClosureAware.KeepReturnType.class))
                .returnTypeDisplayValue(StringUtils.EMPTY)
                .cursorOffset(2)
                .build());

        listTrie.insert(Suggestion.create(CLOSURE)
                .insertValue("eachWithIndex { it, i ->  }")
                .tailText("{ it, i ->  }")
                .lookupToken("eachWithIndex")
                .returnType(TypeProxy.create(ClosureAware.KeepReturnType.class))
                .returnTypeDisplayValue(StringUtils.EMPTY)
                .cursorOffset(2)
                .build());

        listTrie.insert(Suggestion.create(CLOSURE)
                .insertValue("collect { it }")
                .tailText("{ it }")
                .lookupToken("collect")
                .returnType(TypeProxy.create(List.class))
                .returnTypeDisplayValue(StringUtils.EMPTY)
                .cursorOffset(2)
                .build());

        listTrie.insert(Suggestion.create(FUNCTION)
                .insertValue("contains()")
                .tailText("(Object object)")
                .returnType(BOOLEAN)
                .returnTypeDisplayValue(BOOLEAN.toSimpleName(typeAndTries))
                .lookupToken("contains")
                .cursorOffset(1)
                .build());

        listTrie.insert(Suggestion.create(FUNCTION)
                .insertValue("get()")
                .tailText("(int index)")
                .returnType(OBJECT)
                .returnTypeDisplayValue(OBJECT.toSimpleName(typeAndTries))
                .lookupToken("get")
                .cursorOffset(1)
                .build());

        listTrie.insert(Suggestion.create(FUNCTION)
                .insertValue("size()")
                .returnType(INT)
                .returnTypeDisplayValue(INT.toSimpleName(typeAndTries))
                .lookupToken("size")
                .build());

        listTrie.insert(Suggestion.create(FUNCTION)
                .insertValue("isEmpty()")
                .tailText("()")
                .returnType(BOOLEAN)
                .returnTypeDisplayValue(BOOLEAN.toSimpleName(typeAndTries))
                .lookupToken("isEmpty")
                .build());


        Trie arrayListTrie =
                new TrieList(ArrayList.class.getName(), List.class.getName(), null, Object.class.getName());
        typeTrieMap.put(ArrayList.class.getName(), arrayListTrie);
    }

    public static class TrieListClosureArguments extends TrieRoot {

        private static final String ARG_IT = "it";
        private static final String ARG_I = "i";

        public TrieListClosureArguments(TypeAndTries typeAndTries, TypeProxy listItemTypeProxy) {
            insert(Suggestion.create(PROPERTY)
                    .returnTypeDisplayValue(listItemTypeProxy.resolve(typeAndTries).toSimpleName(typeAndTries))
                    .returnType(listItemTypeProxy)
                    .insertValue(ARG_IT)
                    .build());

            TypeProxy indexType = INT; // the index is int
            insert(Suggestion.create(PROPERTY)
                    .returnTypeDisplayValue(indexType.resolve(typeAndTries).toSimpleName(typeAndTries))
                    .returnType(indexType)
                    .insertValue(ARG_I)
                    .build());
        }
    }
}
