package com.reedelk.plugin.service.module.impl.component.metadata;

import com.reedelk.plugin.service.module.impl.component.completion.*;

public class TypeListProxy implements TypeProxy {

    private final String type;

    public TypeListProxy(String type) {
        this.type = type;
    }

    @Override
    public boolean isList(TypeAndTries typeAndTries) {
        return false;
    }

    @Override
    public Trie resolve(TypeAndTries typeAndTries) {
        return new TrieListClosure(type, typeAndTries);
    }

    @Override
    public String toSimpleName(TypeAndTries typeAndTries) {
        return TypeUtils.toSimpleName(type, typeAndTries);
    }

    @Override
    public String getTypeFullyQualifiedName() {
        return Closure.class.getName();
    }

    @Override
    public String listItemType(TypeAndTries typeAndTries) {
        return type;
    }
}
