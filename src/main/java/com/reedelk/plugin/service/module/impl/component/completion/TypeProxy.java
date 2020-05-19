package com.reedelk.plugin.service.module.impl.component.completion;

public interface TypeProxy {

    static TypeProxy create(Class<?> typeClazz) {
        return new TypeProxyDefault(typeClazz.getName());
    }

    static TypeProxy create(String typeFullyQualifiedName) {
        return new TypeProxyDefault(typeFullyQualifiedName);
    }

    static TypeProxy createList(String listItemType) {
        return new TypeProxyClosureList(listItemType);
    }

    static TypeProxy createMap(String mapKeyType, String mapValueType) {
        return new TypeProxyClosureMap(mapKeyType, mapValueType);
    }

    boolean isList(TypeAndTries typeAndTries);

    Trie resolve(TypeAndTries typeAndTries);

    String toSimpleName(TypeAndTries typeAndTries);

    String getTypeFullyQualifiedName();

    String listItemType(TypeAndTries typeAndTries);

}
