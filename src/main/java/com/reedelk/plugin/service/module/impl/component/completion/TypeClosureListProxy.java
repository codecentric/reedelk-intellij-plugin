package com.reedelk.plugin.service.module.impl.component.completion;

public class TypeClosureListProxy implements TypeProxy {

    private final String listItemType;

    public TypeClosureListProxy(String listItemType) {
        this.listItemType = listItemType;
    }

    @Override
    public boolean isList(TypeAndTries typeAndTries) {
        return false;
    }

    @Override
    public Trie resolve(TypeAndTries typeAndTries) {
        return new TrieListClosure(listItemType, typeAndTries);
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
