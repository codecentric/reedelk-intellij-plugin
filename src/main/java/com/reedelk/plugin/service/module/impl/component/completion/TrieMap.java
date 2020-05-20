package com.reedelk.plugin.service.module.impl.component.completion;

import java.util.Collection;
import java.util.Collections;

import static com.reedelk.plugin.service.module.impl.component.completion.Suggestion.Type.FUNCTION;
import static com.reedelk.plugin.service.module.impl.component.completion.Suggestion.Type.PROPERTY;
import static java.util.stream.Collectors.toList;

public class TrieMap extends TrieDefault {

    private final String mapKeyType;
    private final String mapValueType;

    public TrieMap(String typeFullyQualifiedName, String extendsType, String displayName, String mapKeyType, String mapValueType) {
        super(typeFullyQualifiedName, extendsType, displayName);
        this.mapKeyType = mapKeyType;
        this.mapValueType = mapValueType;
    }
    // We always return the type, unless it starts with { in that case we return tye closure
    @Override
    public Collection<Suggestion> autocomplete(String word, TypeAndTries typeAndTrieMap) {
        if (word.equals("{")) {
            // beginning of a closure
            TypeProxy typeProxy = new TypeProxyClosureMap(mapKeyType, mapValueType);
            Suggestion build = Suggestion.create(FUNCTION)
                    .insertValue("{")
                    .returnType(typeProxy)
                    .build();
            return Collections.singletonList(SuggestionFactory.copyWithType(typeAndTrieMap, build, typeProxy));
        }
        Collection<Suggestion> autocomplete = super.autocomplete(word, typeAndTrieMap);
        return autocomplete.stream().map(suggestion ->
                SuggestionFactory.copyWithType(typeAndTrieMap, suggestion, TypeProxy.create(fullyQualifiedName)))
                .collect(toList());
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
            return new TrieMapClosure(typeAndTries);
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

        private class TrieMapClosure extends TrieDefault {

            public TrieMapClosure(TypeAndTries typeAndTries) {
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
}
