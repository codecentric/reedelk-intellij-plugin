package com.reedelk.plugin.service.module.impl.component.completion;

import java.util.Collection;
import java.util.stream.Collectors;

public class TrieMap extends TrieDefault {

    private final String mapKeyType;
    private final String mapValueType;

    public TrieMap(String extendsType, String displayName, String mapKeyType, String mapValueType) {
        super(extendsType, displayName);
        this.mapKeyType = mapKeyType;
        this.mapValueType = mapValueType;
    }

    @Override
    public Collection<Suggestion> autocomplete(String word, TypeAndTries typeAndTrieMap) {
        Collection<Suggestion> autocomplete = super.autocomplete(word, typeAndTrieMap);
        return autocomplete.stream().map(suggestion -> {
            // If it is a closure, we must return the type of the closure arguments, such as it, entry ...
            if (TypeMapClosure.class.getName().equals(suggestion.getReturnType().getTypeFullyQualifiedName())) {
                TypeProxy typeProxy = TypeProxy.createMap(mapKeyType, mapValueType);
                return SuggestionFactory.copyWithType(typeAndTrieMap, suggestion, typeProxy);
            }
            return suggestion;
        }).collect(Collectors.toList());
    }
}
