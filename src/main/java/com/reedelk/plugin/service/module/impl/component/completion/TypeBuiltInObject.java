package com.reedelk.plugin.service.module.impl.component.completion;

import java.util.Map;

import static com.reedelk.plugin.service.module.impl.component.completion.Suggestion.Type.FUNCTION;

class TypeBuiltInObject implements TypeBuiltIn.BuiltInType {

    @Override
    public void register(Map<String, Trie> typeTrieMap, TypeAndTries typeAndTries) {
        Trie objectTrie = new TrieDefault(Object.class.getName(), null, Object.class.getSimpleName());
        typeTrieMap.put(Object.class.getName(), objectTrie);

        objectTrie.insert(Suggestion.create(FUNCTION)
                .lookupToken("toString")
                .insertValue("toString()")
                .tailText("()")
                .returnType(TypeBuiltInPrimitive.STRING)
                .returnTypeDisplayValue(TypeBuiltInPrimitive.STRING.toSimpleName(typeAndTries))
                .build());
    }
}
