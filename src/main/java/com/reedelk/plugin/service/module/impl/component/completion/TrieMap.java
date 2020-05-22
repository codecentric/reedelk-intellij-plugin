package com.reedelk.plugin.service.module.impl.component.completion;

import java.util.Collection;
import java.util.Map;

import static com.reedelk.runtime.api.commons.StringUtils.isNotBlank;
import static java.util.stream.Collectors.toList;

public class TrieMap extends TrieDefault {

    private static final String FORMAT_MAP = "Map<%s,%s>";

    private final String mapKeyType;
    private final String mapValueType;

    public TrieMap(String typeFullyQualifiedName, String displayName, String mapKeyType, String mapValueType) {
        super(typeFullyQualifiedName, Map.class.getName(), displayName);
        this.mapKeyType = mapKeyType;
        this.mapValueType = mapValueType;
    }

    public TrieMap(String typeFullyQualifiedName, String mapKeyType, String mapValueType) {
        this(typeFullyQualifiedName, null, mapKeyType, mapValueType);
    }

    // We always return the type, unless it starts with { in that case we return tye closure
    @Override
    public Collection<Suggestion> autocomplete(String token, TypeAndTries typeAndTrieMap) {
        Collection<Suggestion> autocomplete = super.autocomplete(token, typeAndTrieMap);
        return autocomplete.stream().map(suggestion -> {
            if (suggestion.getType() == Suggestion.Type.CLOSURE) {
                // If the method accepts a closure in input
                TypeProxy mapTypeProxy = new TypeProxyClosureMap();
                ClosureAware.TypeClosureAware newType = new ClosureAware.TypeClosureAware(fullyQualifiedName, mapTypeProxy);
                return SuggestionFactory.copyWithType(typeAndTrieMap, suggestion, newType);
            } else {
                return suggestion;
            }
        }).collect(toList());
    }

    @Override
    public TypeProxy mapKeyType(TypeAndTries typeAndTries) {
        return TypeProxy.create(mapKeyType);
    }

    @Override
    public TypeProxy mapValueType(TypeAndTries typeAndTries) {
        return TypeProxy.create(mapValueType);
    }

    @Override
    public String toSimpleName(TypeAndTries typeAndTries) {
        if (isNotBlank(displayName)) {
            return displayName;
        } else {
            String keyTypeSimpleName = mapKeyType(typeAndTries).toSimpleName(typeAndTries);
            String valueTypeSimpleName = mapValueType(typeAndTries).toSimpleName(typeAndTries);
            return String.format(FORMAT_MAP, keyTypeSimpleName, valueTypeSimpleName);
        }
    }

    private class TypeProxyClosureMap extends TypeProxyDefault {

        TypeProxyClosureMap() {
            super(TypeProxyClosureMap.class.getName());
        }

        @Override
        public Trie resolve(TypeAndTries typeAndTries) {
            TypeProxy mapValueTypeProxy = TypeProxy.create(mapValueType);
            TypeProxy mapKeyTypeProxy = TypeProxy.create(mapKeyType);
            return new TypeDefault.TypeMap.TrieMapClosureArguments(mapKeyTypeProxy, mapValueTypeProxy, typeAndTries);
        }
    }
}
