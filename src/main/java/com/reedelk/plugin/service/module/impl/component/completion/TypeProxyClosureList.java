package com.reedelk.plugin.service.module.impl.component.completion;

class TypeProxyClosureList implements TypeProxy {

    private final String listItemType;

    TypeProxyClosureList(String listItemType) {
        this.listItemType = listItemType;
    }

    @Override
    public boolean isList(TypeAndTries typeAndTries) {
        return false;
    }

    @Override
    public Trie resolve(TypeAndTries typeAndTries) {
        return new TrieListClosure(typeAndTries);
    }

    @Override
    public String toSimpleName(TypeAndTries typeAndTries) {
        return TypeUtils.toSimpleName(listItemType, typeAndTries);
    }

    @Override
    public String getTypeFullyQualifiedName() {
        return TypeMapClosure.class.getName();
    }

    @Override
    public String listItemType(TypeAndTries typeAndTries) {
        return listItemType;
    }

    private class TrieListClosure extends TrieDefault {

        public TrieListClosure(TypeAndTries typeAndTries) {
            TypeProxy listItemTypeProxy = TypeProxy.create(listItemType);
            insert(Suggestion.create(Suggestion.Type.PROPERTY)
                    .returnTypeDisplayValue(listItemTypeProxy.toSimpleName(typeAndTries))
                    .returnType(listItemTypeProxy)
                    .insertValue("it")
                    .build());

            TypeProxy indexType = TypeProxy.create(int.class);
            insert(Suggestion.create(Suggestion.Type.PROPERTY)
                    .returnTypeDisplayValue(indexType.toSimpleName(typeAndTries))
                    .returnType(indexType)
                    .insertValue("i")
                    .build());
        }
    }
}
