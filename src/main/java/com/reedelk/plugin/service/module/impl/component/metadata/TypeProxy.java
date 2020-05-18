package com.reedelk.plugin.service.module.impl.component.metadata;

import com.reedelk.plugin.service.module.impl.component.completion.Trie;
import com.reedelk.plugin.service.module.impl.component.completion.TypeAndTries;
import com.reedelk.plugin.service.module.impl.component.completion.TypeUtils;

import java.util.Objects;

import static com.reedelk.runtime.api.commons.Preconditions.checkState;
import static com.reedelk.runtime.api.commons.StringUtils.isNotBlank;

public interface TypeProxy {

    static TypeProxy create(Class<?> typeClazz) {
        return new TypeProxyDefault(typeClazz.getName());
    }

    static TypeProxy create(String typeFullyQualifiedName) {
        return new TypeProxyDefault(typeFullyQualifiedName);
    }

    boolean isList(TypeAndTries typeAndTries);

    Trie resolve(TypeAndTries typeAndTries);

    String toSimpleName(TypeAndTries typeAndTries);

    String getTypeFullyQualifiedName();

    String listItemType(TypeAndTries typeAndTries);

    class TypeProxyDefault implements TypeProxy {

        private String typeFullyQualifiedName;

        TypeProxyDefault(String typeFullyQualifiedName) {
            checkState(isNotBlank(typeFullyQualifiedName), "null type fully qualified name");
            this.typeFullyQualifiedName = typeFullyQualifiedName;
        }

        @Override
        public boolean isList(TypeAndTries typeAndTries) {
            String listItemType = listItemType(typeAndTries);
            return isNotBlank(listItemType);
        }

        @Override
        public String listItemType(TypeAndTries typeAndTries) {
            Trie orDefault = resolve(typeAndTries);
            return orDefault.listItemType();
        }

        @Override
        public Trie resolve(TypeAndTries typeAndTries) {
            return typeAndTries.getOrDefault(typeFullyQualifiedName);
        }

        @Override
        public String toSimpleName(TypeAndTries typeAndTries) {
            return TypeUtils.toSimpleName(typeFullyQualifiedName, typeAndTries);
        }

        @Override
        public String getTypeFullyQualifiedName() {
            return typeFullyQualifiedName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TypeProxyDefault that = (TypeProxyDefault) o;
            return typeFullyQualifiedName.equals(that.typeFullyQualifiedName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(typeFullyQualifiedName);
        }
    }
}
