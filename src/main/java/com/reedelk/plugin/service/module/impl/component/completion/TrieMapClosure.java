package com.reedelk.plugin.service.module.impl.component.completion;

public class TrieMapClosure extends TrieImpl {

    public TrieMapClosure(String mapKeyType, String mapValueType, TypeAndTries typeAndTries) {

        insert(Suggestion.create(Suggestion.Type.PROPERTY)
                .lookupToken("entry")
                .insertValue("entry")
                .returnTypeDisplayValue(TypeProxy.create(mapValueType).toSimpleName(typeAndTries))
                .returnType(TypeProxy.create(mapValueType))
                .build());

        insert(Suggestion.create(Suggestion.Type.PROPERTY)
                .lookupToken("i")
                .insertValue("i")
                .returnTypeDisplayValue(TypeProxy.create(mapKeyType).toSimpleName(typeAndTries))
                .returnType(TypeProxy.create(mapKeyType))
                .build());
    }
}
