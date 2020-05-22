package com.reedelk.plugin.service.module.impl.component.completion;

import java.util.Collection;
import java.util.List;

import static com.reedelk.plugin.service.module.impl.component.completion.Suggestion.Type.CLOSURE;
import static com.reedelk.runtime.api.commons.Preconditions.checkNotNull;
import static com.reedelk.runtime.api.commons.StringUtils.isNotBlank;
import static java.util.stream.Collectors.toList;

public class TrieList extends TrieDefault {

    private static final String FORMAT_LIST = "List<%s>";

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
    public TypeProxy listItemType(TypeAndTries typeAndTries) {
        return TypeProxy.create(listItemType);
    }

    @Override
    public Collection<Suggestion> autocomplete(String token, TypeAndTries typeAndTrieMap) {
        Collection<Suggestion> autocomplete = super.autocomplete(token, typeAndTrieMap);
        return autocomplete.stream().map(suggestion -> {
            if (suggestion.getType() == CLOSURE) {
                // If the method accepts a closure in input
                TypeProxy listTypeProxy = new TypeProxyClosureList();
                ClosureAware.TypeClosureAware newType = new ClosureAware.TypeClosureAware(fullyQualifiedName, listTypeProxy);
                return SuggestionFactory.copyWithType(typeAndTrieMap, suggestion, newType);
            } else {
                return suggestion;
            }
        }).collect(toList());
    }

    @Override
    public String toSimpleName(TypeAndTries typeAndTries) {
        if (isNotBlank(displayName)) {
            return displayName;
        } else {
            String listItemTypeSimpleName = listItemType(typeAndTries).toSimpleName(typeAndTries);
            return String.format(FORMAT_LIST, listItemTypeSimpleName);
        }
    }

    private class TypeProxyClosureList extends TypeProxyDefault {

        TypeProxyClosureList() {
            super(TypeProxyClosureList.class.getName());
        }

        @Override
        public Trie resolve(TypeAndTries typeAndTries) {
            TypeProxy listItemTypeProxy = TypeProxy.create(listItemType);
            return new TypeDefault.TypeList.TrieListClosureArguments(typeAndTries, listItemTypeProxy);
        }
    }
}
