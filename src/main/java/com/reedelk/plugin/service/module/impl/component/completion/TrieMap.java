package com.reedelk.plugin.service.module.impl.component.completion;

import java.util.Collection;
import java.util.Map;

import static com.reedelk.plugin.service.module.impl.component.completion.Suggestion.Type.PROPERTY;
import static java.util.stream.Collectors.toList;

public class TrieMap extends TrieDefault {

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


    private class TypeProxyClosureMap implements TypeProxy {

        @Override
        public Trie resolve(TypeAndTries typeAndTries) {
            return new TrieMapClosureArguments(mapValueType, mapKeyType, typeAndTries);
        }

        @Override
        public String toSimpleName(TypeAndTries typeAndTries) {
            return FullyQualifiedName.formatMap(mapKeyType, mapValueType, typeAndTries);
        }

        @Override
        public String getTypeFullyQualifiedName() {
            return TypeProxyClosureMap.class.getName();
        }
    }

    static class TrieMapClosureArguments extends TrieDefault {

        public TrieMapClosureArguments(String mapValueType, String mapKeyType, TypeAndTries typeAndTries) {

            TypeProxy mapValueTypeProxy = TypeProxy.create(mapValueType);
            insert(Suggestion.create(PROPERTY)
                    .returnTypeDisplayValue(mapValueTypeProxy.toSimpleName(typeAndTries))
                    .returnType(mapValueTypeProxy)
                    .insertValue("entry")
                    .build());

            TypeProxy mapKeyTypeProxy = TypeProxy.create(mapKeyType);
            insert(Suggestion.create(PROPERTY)
                    .returnTypeDisplayValue(mapKeyTypeProxy.toSimpleName(typeAndTries))
                    .returnType(mapKeyTypeProxy)
                    .insertValue("i")
                    .build());
        }
    }
}
