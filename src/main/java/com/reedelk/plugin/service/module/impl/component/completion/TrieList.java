package com.reedelk.plugin.service.module.impl.component.completion;

import java.util.Collection;
import java.util.List;

import static com.reedelk.runtime.api.commons.Preconditions.checkNotNull;
import static java.util.stream.Collectors.toList;

public class TrieList extends TrieDefault {

    private final String listItemType;

    public TrieList(String typeFullyQualifiedName, String displayName, String listItemType) {
        super(typeFullyQualifiedName, List.class.getName(), displayName);
        checkNotNull(listItemType, "listItemType");
        this.listItemType = listItemType;
    }

    public TrieList(String typeFullyQualifiedName, String listItemType) {
        this(typeFullyQualifiedName, null, listItemType);
    }

    @Override
    public String listItemType() {
        return listItemType;
    }

    @Override
    public Collection<Suggestion> autocomplete(String word, TypeAndTries typeAndTrieMap) {
        Collection<Suggestion> autocomplete = super.autocomplete(word, typeAndTrieMap);
        return autocomplete.stream().map(suggestion -> {
            if (suggestion.getType() == Suggestion.Type.CLOSURE) {
                // If the method accepts a closure in input
                TypeProxy listTypeProxy = new TypeProxyClosureList();
                ClosureAware.TypeClosureAware newType = new ClosureAware.TypeClosureAware(fullyQualifiedName, listTypeProxy);
                return SuggestionFactory.copyWithType(typeAndTrieMap, suggestion, newType);
            } else {
                return suggestion;
            }
        }).collect(toList());
    }

    private class TypeProxyClosureList implements TypeProxy {

        @Override
        public Trie resolve(TypeAndTries typeAndTries) {
            return new TrieListClosureArguments(typeAndTries);
        }

        @Override
        public String toSimpleName(TypeAndTries typeAndTries) {
            return FullyQualifiedName.formatList(listItemType, typeAndTries);
        }

        @Override
        public String getTypeFullyQualifiedName() {
            return TypeProxyClosureList.class.getName();
        }
    }

    private class TrieListClosureArguments extends TrieDefault {

        public TrieListClosureArguments(TypeAndTries typeAndTries) {
            TypeProxy listItemTypeProxy = TypeProxy.create(listItemType);
            insert(Suggestion.create(Suggestion.Type.PROPERTY)
                    .returnTypeDisplayValue(listItemTypeProxy.toSimpleName(typeAndTries))
                    .returnType(listItemTypeProxy)
                    .insertValue("it")
                    .build());

            TypeProxy indexType = TypeProxy.create(int.class);
            insert(Suggestion.create(Suggestion.Type.PROPERTY)
                    .returnTypeDisplayValue(indexType.toSimpleName(typeAndTries))
                    .returnType(indexType)
                    .insertValue("i")
                    .build());
        }
    }
}
