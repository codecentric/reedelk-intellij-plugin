package com.reedelk.plugin.service.module.impl.component.completion;

import java.util.Collection;
import java.util.Map;

import static com.reedelk.plugin.service.module.impl.component.completion.Suggestion.Type.PROPERTY;
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
        this(typeFullyQualifiedName, Map.class.getSimpleName(), mapKeyType, mapValueType);
    }

    // We always return the type, unless it starts with { in that case we return tye closure
    @Override
    public Collection<Suggestion> autocomplete(String word, TypeAndTries typeAndTrieMap) {
        Collection<Suggestion> autocomplete = super.autocomplete(word, typeAndTrieMap);
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
        String keyTypeSimpleName = mapKeyType(typeAndTries).toSimpleName(typeAndTries);
        String valueTypeSimpleName = mapValueType(typeAndTries).toSimpleName(typeAndTries);
        return String.format(FORMAT_MAP, keyTypeSimpleName, valueTypeSimpleName);
    }

    private class TypeProxyClosureMap extends TypeProxyDefault {

        TypeProxyClosureMap() {
            super(TypeProxyClosureMap.class.getName());
        }

        @Override
        public Trie resolve(TypeAndTries typeAndTries) {
            TypeProxy mapValueTypeProxy = TypeProxy.create(mapValueType);
            TypeProxy mapKeyTypeProxy = TypeProxy.create(mapKeyType);
            return new TrieMapClosureArguments(mapKeyTypeProxy, mapValueTypeProxy, typeAndTries);
        }
    }

    static class TrieMapClosureArguments extends TrieDefault {

        private static final String ARG_ENTRY = "entry";
        private static final String ARG_I = "i";

        public TrieMapClosureArguments(TypeProxy mapValueType, TypeProxy mapKeyType, TypeAndTries typeAndTries) {
            insert(Suggestion.create(PROPERTY)
                    .returnTypeDisplayValue(mapValueType.resolve(typeAndTries).toSimpleName(typeAndTries))
                    .returnType(mapValueType)
                    .insertValue(ARG_ENTRY)
                    .build());

            insert(Suggestion.create(PROPERTY)
                    .returnTypeDisplayValue(mapKeyType.resolve(typeAndTries).toSimpleName(typeAndTries))
                    .returnType(mapKeyType)
                    .insertValue(ARG_I)
                    .build());
        }
    }
}
