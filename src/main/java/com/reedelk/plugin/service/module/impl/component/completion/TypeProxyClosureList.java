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
        return new TrieList(null, null, listItemType, typeAndTries);
    }

    @Override
    public String toSimpleName(TypeAndTries typeAndTries) {
        return TypeUtils.toSimpleName(listItemType, typeAndTries);
    }

    @Override
    public String getTypeFullyQualifiedName() {
        return TypeClosure.class.getName();
    }

    @Override
    public String listItemType(TypeAndTries typeAndTries) {
        return listItemType;
    }
}
