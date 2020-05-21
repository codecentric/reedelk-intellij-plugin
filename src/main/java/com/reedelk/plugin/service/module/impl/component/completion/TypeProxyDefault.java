package com.reedelk.plugin.service.module.impl.component.completion;

import java.util.Objects;

import static com.reedelk.runtime.api.commons.Preconditions.checkState;
import static com.reedelk.runtime.api.commons.StringUtils.isNotBlank;

public class TypeProxyDefault implements TypeProxy {

    protected String typeFullyQualifiedName;

    public TypeProxyDefault(String typeFullyQualifiedName) {
        checkState(isNotBlank(typeFullyQualifiedName), "null type fully qualified name");
        this.typeFullyQualifiedName = typeFullyQualifiedName;
    }

    @Override
    public Trie resolve(TypeAndTries typeAndTries) {
        return typeAndTries.getOrDefault(typeFullyQualifiedName);
    }

    @Override
    public String getTypeFullyQualifiedName() {
        return typeFullyQualifiedName;
    }

    @Override
    public String toSimpleName(TypeAndTries typeAndTries) {
        return resolve(typeAndTries).toSimpleName(typeAndTries);
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
