package com.reedelk.plugin.service.module.impl.component.completion;

// TODO: This should be generated from defaults
public class TrieListClosure extends TrieImpl {

    public TrieListClosure(String listItemType, TypeAndTries typeAndTries) {
        TypeProxy listItemTypeProxy = TypeProxy.create(listItemType);
        insert(Suggestion.create(Suggestion.Type.PROPERTY)
                .lookupToken("it")
                .insertValue("it")
                .returnTypeDisplayValue(listItemTypeProxy.toSimpleName(typeAndTries))
                .returnType(listItemTypeProxy)
                .build());

        TypeProxy indexType = TypeProxy.create(int.class);
        insert(Suggestion.create(Suggestion.Type.PROPERTY)
                .returnTypeDisplayValue(indexType.toSimpleName(typeAndTries))
                .returnType(indexType)
                .lookupToken("i")
                .insertValue("i")
                .build());
    }
}
