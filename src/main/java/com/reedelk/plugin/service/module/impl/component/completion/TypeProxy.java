package com.reedelk.plugin.service.module.impl.component.completion;

public interface TypeProxy {

    static TypeProxy create(Class<?> typeClazz) {
        return new TypeProxyDefault(typeClazz.getName());
    }

    static TypeProxy create(String typeFullyQualifiedName) {
        return new TypeProxyDefault(typeFullyQualifiedName);
    }


    Trie resolve(TypeAndTries typeAndTries);

    String toSimpleName(TypeAndTries typeAndTries);

    String getTypeFullyQualifiedName();

    String listItemType(TypeAndTries typeAndTries);

    default boolean isList(TypeAndTries typeAndTries) {
        return false;
    }

    default boolean isDynamic() {
        return false;
    }

}
