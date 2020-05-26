package com.reedelk.plugin.service.module.impl.component.completion;

import java.util.Collection;

import static com.reedelk.plugin.service.module.impl.component.completion.ClosureAware.KeepReturnType;
import static com.reedelk.plugin.service.module.impl.component.completion.ClosureAware.TypeClosureAware;
import static com.reedelk.plugin.service.module.impl.component.completion.Suggestion.Type.CLOSURE;
import static com.reedelk.runtime.api.commons.Preconditions.checkNotNull;
import static com.reedelk.runtime.api.commons.StringUtils.isNotBlank;
import static java.util.stream.Collectors.toList;

public class TrieList extends TrieDefault {

    private static final String FORMAT_LIST = "List<%s>";

    public static String formatList(String listItemType) {
        return String.format(FORMAT_LIST, listItemType);
    }

    private final String listItemType;

    public TrieList(String typeFullyQualifiedName, String extendsType, String displayName, String listItemType) {
        super(typeFullyQualifiedName, extendsType, displayName);
        checkNotNull(listItemType, "listItemType");
        this.listItemType = listItemType;
    }

    @Override
    public Collection<Suggestion> autocomplete(String token, TypeAndTries typeAndTrieMap) {
        Collection<Suggestion> autocomplete = super.autocomplete(token, typeAndTrieMap);
        return autocomplete.stream().map(suggestion -> {

            if (suggestion.getType() != CLOSURE) {
                return suggestion;
            }

            // If the method accepts a closure in input we need to replace the suggestion
            // with the closure aware completion type trie. The return type could be the original
            // type (e.g the list) or the user specified type from the suggestion.
            if (KeepReturnType.class.getName().equals(suggestion.getReturnType().getTypeFullyQualifiedName())) {
                TypeProxy closureReturnType = new TypeProxyClosureList();
                TypeClosureAware newType = new TypeClosureAware(fullyQualifiedName, this, closureReturnType);
                return isNotBlank(displayName) ?
                        SuggestionFactory.copyWithTypeAndDisplayName(suggestion, newType, displayName) :
                        SuggestionFactory.copyWithType(typeAndTrieMap, suggestion, newType);

            } else {
                Trie endClosureReturnType = suggestion.getReturnType().resolve(typeAndTrieMap);
                TypeProxy closureReturnType = new TypeProxyClosureList();
                TypeClosureAware newType = new TypeClosureAware(suggestion.getReturnType().getTypeFullyQualifiedName(), endClosureReturnType, closureReturnType);
                return SuggestionFactory.copyWithType(typeAndTrieMap, suggestion, newType);
            }

        }).collect(toList());
    }

    @Override
    public Collection<Suggestion> autocompleteForExtend(String token, TypeAndTries typeAndTrieMap) {
        // We don't replace closure because it has to be replaced
        // with the type of the last child.
        return super.autocomplete(token, typeAndTrieMap);
    }

    @Override
    public boolean isList() {
        return true;
    }

    @Override
    public TypeProxy listItemType(TypeAndTries typeAndTries) {
        return TypeProxy.create(listItemType);
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
