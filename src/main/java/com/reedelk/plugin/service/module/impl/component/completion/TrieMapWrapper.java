package com.reedelk.plugin.service.module.impl.component.completion;

import java.util.Map;

public class TrieMapWrapper  {

    private final Map<String, TypeInfo>[] typeAndTries;

    public TrieMapWrapper(Map<String, TypeInfo> ...typeAndTries) {
        this.typeAndTries = typeAndTries;
    }

    public TypeInfo getOrDefault(String fullyQualifiedTypeName, TypeInfo defaultOne) {
        for (Map<String, TypeInfo> typeAndTrie : typeAndTries) {
            TypeInfo typeInfo = typeAndTrie.get(fullyQualifiedTypeName);
            if (typeInfo != null) return typeInfo;
        }
        return defaultOne;
    }
}
