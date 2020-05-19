package com.reedelk.plugin.service.module.impl.component.completion;

import java.util.Collection;
import java.util.stream.Collectors;

import static com.reedelk.runtime.api.commons.Preconditions.checkNotNull;

public class TrieList extends TrieDefault {

    private final String listItemType;

    public TrieList(String extendsType, String displayName, String listItemType, TypeAndTries typeAndTries) {
        super(extendsType, displayName);
        checkNotNull(listItemType, "listItemType");
        this.listItemType = listItemType;
        initialize(typeAndTries);
    }

    @Override
    public Collection<Suggestion> autocomplete(String word, TypeAndTries typeAndTrieMap) {
        Collection<Suggestion> autocomplete = super.autocomplete(word, typeAndTrieMap);
        return autocomplete.stream().map(suggestion -> {
            // If it is a closure, we must return the type of the closure arguments, such as it, entry ...
            if (TypeClosure.class.getName().equals(suggestion.getReturnType().getTypeFullyQualifiedName())) {
                TypeProxy typeProxy = TypeProxy.createList(listItemType);
                return SuggestionFactory.copyWithType(typeAndTrieMap, suggestion, typeProxy);
            }
            return suggestion;
        }).collect(Collectors.toList());
    }

    // TODO: This should be generated from defaults, add types for list
    private void initialize(TypeAndTries typeAndTries) {
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
