package com.reedelk.plugin.service.module.impl.component.completion;

import com.reedelk.runtime.api.message.MessageAttributes;
import com.reedelk.runtime.api.message.MessagePayload;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static com.reedelk.runtime.api.commons.Preconditions.checkState;
import static com.reedelk.runtime.api.commons.StringUtils.isBlank;
import static com.reedelk.runtime.api.commons.StringUtils.isNotBlank;

class TypeProxyDefault implements TypeProxy {

    private String typeFullyQualifiedName;

    TypeProxyDefault(String typeFullyQualifiedName) {
        checkState(isNotBlank(typeFullyQualifiedName), "null type fully qualified name");
        this.typeFullyQualifiedName = typeFullyQualifiedName;
    }

    @Override
    public boolean isList(TypeAndTries typeAndTries) {
        TypeProxy listItemType = listItemType(typeAndTries);
        return listItemType != null;
    }

    @Override
    public boolean isDynamic() {
        return MessagePayload.class.getName().equals(typeFullyQualifiedName) ||
                MessageAttributes.class.getName().equals(typeFullyQualifiedName);
    }

    @Override
    @Nullable
    public TypeProxy listItemType(TypeAndTries typeAndTries) {
        Trie listItemTypeTrie = resolve(typeAndTries);
        String listItemType = listItemTypeTrie.listItemType();
        return isBlank(listItemType) ? null : TypeProxy.create(listItemType);
    }

    @Override
    public Trie resolve(TypeAndTries typeAndTries) {
        return typeAndTries.getOrDefault(typeFullyQualifiedName);
    }

    @Override
    public String toSimpleName(TypeAndTries typeAndTries) {
        Trie typeTrie = resolve(typeAndTries);
        if (MessageAttributes.class.getName().equals(typeTrie.extendsType())) {
            // We keep the message attributes type simple name to avoid confusion and always display 'MessageAttributes' type.
            return MessageAttributes.class.getSimpleName();
        } else {
            // In any other case
            return FullyQualifiedName.toSimpleName(typeFullyQualifiedName, typeAndTries);
        }
    }

    @Override
    public String getTypeFullyQualifiedName() {
        return typeFullyQualifiedName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !o.getClass().isAssignableFrom(this.getClass())) return false;
        TypeProxyDefault that = (TypeProxyDefault) o;
        return Objects.equals(typeFullyQualifiedName, that.typeFullyQualifiedName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(typeFullyQualifiedName);
    }

    @Override
    public String toString() {
        return "TypeProxyDefault{" +
                "typeFullyQualifiedName='" + typeFullyQualifiedName + '\'' +
                '}';
    }
}
