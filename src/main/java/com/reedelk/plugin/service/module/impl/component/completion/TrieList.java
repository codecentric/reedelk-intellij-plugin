package com.reedelk.plugin.service.module.impl.component.completion;

import java.util.Collection;
import java.util.stream.Collectors;

import static com.reedelk.runtime.api.commons.Preconditions.checkNotNull;

public class TrieList extends TrieDefault {

    private final String listItemType;

    public TrieList(String extendsType, String displayName, String listItemType) {
        super(extendsType, displayName);
        checkNotNull(listItemType, "listItemType");
        this.listItemType = listItemType;
    }

    @Override
    public Collection<Suggestion> autocomplete(String word, TypeAndTries typeAndTrieMap) {
        Collection<Suggestion> autocomplete = super.autocomplete(word, typeAndTrieMap);
        return autocomplete.stream().map(suggestion -> {
            // If it is a closure, we must return the type of the closure arguments, such as it, entry ...
            if (TypeListClosure.class.getName().equals(suggestion.getReturnType().getTypeFullyQualifiedName())) {
                TypeProxy typeProxy = TypeProxy.createList(listItemType);
                return SuggestionFactory.copyWithType(typeAndTrieMap, suggestion, typeProxy);
            }
            return suggestion;
        }).collect(Collectors.toList());
    }
}
