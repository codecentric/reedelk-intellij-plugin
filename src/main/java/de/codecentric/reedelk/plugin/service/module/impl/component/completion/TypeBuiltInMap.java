package de.codecentric.reedelk.plugin.service.module.impl.component.completion;

import de.codecentric.reedelk.runtime.api.commons.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import static de.codecentric.reedelk.plugin.service.module.impl.component.completion.TypeBuiltInPrimitive.*;

class TypeBuiltInMap implements TypeBuiltIn.BuiltInType {

    @Override
    public void register(Map<String, Trie> typeTrieMap, TypeAndTries typeAndTries) {
        Trie mapTrie = new TrieMap(
                Map.class.getName(),
                Object.class.getName(),
                null,
                Object.class.getName(),
                Object.class.getName());
        typeTrieMap.put(Map.class.getName(), mapTrie);

        mapTrie.insert(Suggestion.create(Suggestion.Type.CLOSURE)
                .insertValue("each { entry }")
                .lookupToken("each")
                .tailText("{ entry }")
                .returnType(TypeProxy.create(ClosureAware.KeepReturnType.class))
                .returnTypeDisplayValue(StringUtils.EMPTY)
                .cursorOffset(2)
                .build());

        mapTrie.insert(Suggestion.create(Suggestion.Type.CLOSURE)
                .insertValue("eachWithIndex { entry, i ->  }")
                .tailText("{ entry, i ->  }")
                .lookupToken("eachWithIndex")
                .returnType(TypeProxy.create(ClosureAware.KeepReturnType.class))
                .returnTypeDisplayValue(StringUtils.EMPTY)
                .cursorOffset(2)
                .build());

        mapTrie.insert(Suggestion.create(Suggestion.Type.FUNCTION)
                .insertValue("get()")
                .tailText("(Object key)")
                .returnType(OBJECT)
                .returnTypeDisplayValue(OBJECT.toSimpleName(typeAndTries))
                .lookupToken("get")
                .cursorOffset(1)
                .build());

        mapTrie.insert(Suggestion.create(Suggestion.Type.FUNCTION)
                .insertValue("size()")
                .returnType(INT)
                .returnTypeDisplayValue(INT.toSimpleName(typeAndTries))
                .lookupToken("size")
                .build());

        mapTrie.insert(Suggestion.create(Suggestion.Type.FUNCTION)
                .insertValue("isEmpty()")
                .tailText("()")
                .returnType(BOOLEAN)
                .returnTypeDisplayValue(BOOLEAN.toSimpleName(typeAndTries))
                .lookupToken("isEmpty")
                .build());

        Trie hashMap =
                new TrieMap(HashMap.class.getName(), Map.class.getName(), null, Object.class.getName(), Object.class.getName());
        typeTrieMap.put(HashMap.class.getName(), hashMap);

        Trie treeMap = new TrieMap(
                TreeMap.class.getName(), Map.class.getName(), null, Object.class.getName(), Object.class.getName());
        typeTrieMap.put(TreeMap.class.getName(), treeMap);
    }

    public static class TrieMapClosureArguments extends TrieRoot {

        private static final String ARG_ENTRY = "entry";
        private static final String ARG_I = "i";

        public TrieMapClosureArguments(TypeProxy mapKeyType, TypeProxy mapValueType, TypeAndTries typeAndTries) {
            insert(Suggestion.create(Suggestion.Type.PROPERTY)
                    .returnTypeDisplayValue(mapValueType.resolve(typeAndTries).toSimpleName(typeAndTries))
                    .returnType(mapValueType)
                    .insertValue(ARG_ENTRY)
                    .build());

            insert(Suggestion.create(Suggestion.Type.PROPERTY)
                    .returnTypeDisplayValue(mapKeyType.resolve(typeAndTries).toSimpleName(typeAndTries))
                    .returnType(mapKeyType)
                    .insertValue(ARG_I)
                    .build());
        }
    }
}
