package com.reedelk.plugin.service.module.impl.component.completion;

import com.reedelk.plugin.service.module.impl.component.metadata.TypeProxy;

// TODO: This should be generated from defaults
public class TrieListClosure extends TrieImpl {

    public TrieListClosure(String type, TypeAndTries typeAndTries) {
        insert(Suggestion.create(Suggestion.Type.PROPERTY)
                .lookupToken("it")
                .insertValue("it")
                .returnTypeDisplayValue(TypeProxy.create(type).toSimpleName(typeAndTries))
                .returnType(TypeProxy.create(type))
                .build());
    }
}
