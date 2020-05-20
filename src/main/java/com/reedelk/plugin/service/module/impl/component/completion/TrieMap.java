package com.reedelk.plugin.service.module.impl.component.completion;

import java.util.Collection;
import java.util.Map;

import static com.reedelk.plugin.service.module.impl.component.completion.Suggestion.Type.FUNCTION;
import static com.reedelk.plugin.service.module.impl.component.completion.Suggestion.Type.PROPERTY;
import static java.util.Collections.singletonList;
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
                TypeClosureAware newType = new TypeClosureAware(fullyQualifiedName, mapKeyType, mapValueType);
                return SuggestionFactory.copyWithType(typeAndTrieMap, suggestion, newType);
            } else {
                return SuggestionFactory.copyWithType(typeAndTrieMap, suggestion, TypeProxy.create(fullyQualifiedName));
            }
        }).collect(toList());
    }


    private static class TypeClosureAware extends TypeProxyDefault {

        private final String mapKeyType;
        private final String mapValueType;

        TypeClosureAware(String typeFullyQualifiedName, String mapKeyType, String mapValueType) {
            super(typeFullyQualifiedName);
            this.mapKeyType = mapKeyType;
            this.mapValueType = mapValueType;
        }

        @Override
        public boolean isList(TypeAndTries typeAndTries) {
            return false;
        }

        @Override
        public Trie resolve(TypeAndTries typeAndTries) {
            Trie orDefault = typeAndTries.getOrDefault(getTypeFullyQualifiedName());
            return new TrieClosureAware(orDefault, mapKeyType, mapValueType);
        }
    }

    private static class TypeProxyClosureMap implements TypeProxy {

        private final String mapKeyType;
        private final String mapValueType;

        TypeProxyClosureMap(String mapKeyType, String mapValueType) {
            this.mapKeyType = mapKeyType;
            this.mapValueType = mapValueType;
        }

        @Override
        public boolean isList(TypeAndTries typeAndTries) {
            return false;
        }

        @Override
        public Trie resolve(TypeAndTries typeAndTries) {
            return new TrieMapClosure(mapValueType, mapKeyType, typeAndTries);
        }

        @Override
        public String toSimpleName(TypeAndTries typeAndTries) {
            String keyTypeSimpleName = TypeUtils.toSimpleName(mapKeyType, typeAndTries);
            String valueTypeSimpleName = TypeUtils.toSimpleName(mapValueType, typeAndTries);
            return String.format("Map<%s,%s>", keyTypeSimpleName, valueTypeSimpleName);
        }

        @Override
        public String getTypeFullyQualifiedName() {
            return TypeMapClosure.class.getName();
        }

        @Override
        public String listItemType(TypeAndTries typeAndTries) {
            return null;
        }
    }

    private static class TrieClosureAware extends TrieDefault {

        private final String mapKeyType;
        private final String mapValueType;
        private final Trie trieDefault;

        public TrieClosureAware(Trie trieDefault, String mapKeyType, String mapValueType) {
            this.trieDefault = trieDefault;
            this.mapKeyType = mapKeyType;
            this.mapValueType = mapValueType;
        }

        @Override
        public Collection<Suggestion> autocomplete(String word, TypeAndTries typeAndTrieMap) {
            if (word.equals("{")) {
                // beginning of a closure
                TypeProxy typeProxy = new TypeProxyClosureMap(mapKeyType, mapValueType);
                Suggestion build = Suggestion.create(FUNCTION)
                        .insertValue("{")
                        .returnType(typeProxy)
                        .build();
                return singletonList(
                        SuggestionFactory.copyWithType(typeAndTrieMap, build, typeProxy));
            } else {
                return trieDefault.autocomplete(word, typeAndTrieMap);
            }
        }
    }

    static class TrieMapClosure extends TrieDefault {

        public TrieMapClosure(String mapValueType, String mapKeyType, TypeAndTries typeAndTries) {

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
