package com.reedelk.plugin.service.module.impl.component.completion;

import com.reedelk.runtime.api.message.MessageAttributes;
import com.reedelk.runtime.api.message.MessagePayload;

public interface TypeProxy {

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
        return MessagePayload.class.getName().equals(getTypeFullyQualifiedName()) ||
                MessageAttributes.class.getName().equals(getTypeFullyQualifiedName());
    }
}
