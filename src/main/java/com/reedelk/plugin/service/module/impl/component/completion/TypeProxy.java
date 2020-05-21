package com.reedelk.plugin.service.module.impl.component.completion;

public interface TypeProxy {

    TypeProxy VOID = TypeProxy.create(Void.class.getSimpleName());
    TypeProxy FLATTENED = TypeProxy.create(FlattenedReturnType.class);

    static TypeProxy create(Class<?> typeClazz) {
        return new TypeProxyDefault(typeClazz.getName());
    }

    static TypeProxy create(String typeFullyQualifiedName) {
        return new TypeProxyDefault(typeFullyQualifiedName);
    }


    String getTypeFullyQualifiedName();

    String toSimpleName(TypeAndTries typeAndTries);

    Trie resolve(TypeAndTries typeAndTries);

    default boolean isDynamic() {
        return false;
    }

}
