package de.codecentric.reedelk.plugin.service.module.impl.component.completion;

import static de.codecentric.reedelk.runtime.api.commons.Preconditions.checkState;
import static de.codecentric.reedelk.runtime.api.commons.StringUtils.isNotBlank;

public class TypeProxyDefault implements TypeProxy {

    protected String typeFullyQualifiedName;

    public TypeProxyDefault(String typeFullyQualifiedName) {
        checkState(isNotBlank(typeFullyQualifiedName), "blank type fully qualified name");
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
    public String toString() {
        return "TypeProxyDefault{" +
                "typeFullyQualifiedName='" + typeFullyQualifiedName + '\'' +
                '}';
    }
}
