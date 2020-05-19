package com.reedelk.plugin.service.module.impl.component.completion;

import java.util.Collection;
import java.util.stream.Collectors;

import static com.reedelk.plugin.service.module.impl.component.completion.Suggestion.Type.PROPERTY;

public class TrieMap extends TrieDefault {

    private final String mapKeyType;
    private final String mapValueType;

    public TrieMap(String extendsType, String displayName, String mapKeyType, String mapValueType, TypeAndTries typeAndTries) {
        super(extendsType, displayName);
        this.mapKeyType = mapKeyType;
        this.mapValueType = mapValueType;
        initialize(typeAndTries);
    }

    @Override
    public Collection<Suggestion> autocomplete(String word, TypeAndTries typeAndTrieMap) {
        Collection<Suggestion> autocomplete = super.autocomplete(word, typeAndTrieMap);
        return autocomplete.stream().map(suggestion -> {
            // If it is a closure, we must return the type of the closure arguments, such as it, entry ...
            if (TypeClosure.class.getName().equals(suggestion.getReturnType().getTypeFullyQualifiedName())) {
                TypeProxy typeProxy = TypeProxy.createMap(mapKeyType, mapValueType);
                return SuggestionFactory.copyWithType(typeAndTrieMap, suggestion, typeProxy);
            }
            return suggestion;
        }).collect(Collectors.toList());
    }

    // TODO: Add default map suggestions....
    private void initialize(TypeAndTries typeAndTries) {
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
