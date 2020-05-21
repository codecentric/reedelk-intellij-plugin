package com.reedelk.plugin.service.module.impl.component.completion;

import org.jetbrains.annotations.Nullable;

public interface TypeProxy {

    TypeProxy VOID = TypeProxy.create(Void.class.getSimpleName());
    TypeProxy FLATTENED = TypeProxy.create(FlattenedReturnType.class);

    static TypeProxy create(Class<?> typeClazz) {
        return new TypeProxyDefault(typeClazz.getName());
    }

    static TypeProxy create(String typeFullyQualifiedName) {
        return new TypeProxyDefault(typeFullyQualifiedName);
    }


    Trie resolve(TypeAndTries typeAndTries);

    String toSimpleName(TypeAndTries typeAndTries);

    String getTypeFullyQualifiedName();

    @Nullable
    default TypeProxy listItemType(TypeAndTries typeAndTries) {
        return null;
    }

    default boolean isList(TypeAndTries typeAndTries) {
        return false;
    }

    default boolean isDynamic() {
        return false;
    }

}
