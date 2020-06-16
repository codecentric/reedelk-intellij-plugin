package com.reedelk.plugin.service.module.impl.component.completion;

import com.reedelk.runtime.api.message.MessageAttributes;

import java.util.Collection;

import static com.reedelk.plugin.service.module.impl.component.completion.ClosureAware.KeepReturnType;
import static com.reedelk.plugin.service.module.impl.component.completion.ClosureAware.TypeClosureAware;
import static com.reedelk.plugin.service.module.impl.component.completion.Suggestion.Type.CLOSURE;
import static com.reedelk.runtime.api.commons.StringUtils.isNotBlank;
import static java.util.stream.Collectors.toList;

public class TrieMap extends TrieDefault {

    private static final String FORMAT_MAP = "Map<%s,%s>";

    private final String mapKeyType;
    private final String mapValueType;

    public TrieMap(String typeFullyQualifiedName, String extendsType, String displayName, String mapKeyType, String mapValueType) {
        super(typeFullyQualifiedName, extendsType, displayName);
        this.mapKeyType = mapKeyType;
        this.mapValueType = mapValueType;
    }

    // We always return the type, unless it starts with { in that case we return tye closure
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
                TypeProxy mapTypeProxy = new TypeProxyClosureMap();
                TypeClosureAware newType = new TypeClosureAware(fullyQualifiedName, this, mapTypeProxy);
                return isNotBlank(displayName) ?
                        SuggestionFactory.copyWithTypeAndDisplayName(suggestion, newType, displayName) :
                        SuggestionFactory.copyWithType(typeAndTrieMap, suggestion, newType);

            } else {
                Trie endClosureReturnType = suggestion.getReturnType().resolve(typeAndTrieMap);
                TypeProxy mapTypeProxy = new TypeProxyClosureMap();
                TypeClosureAware newType = new TypeClosureAware(suggestion.getReturnType().getTypeFullyQualifiedName(), endClosureReturnType, mapTypeProxy);
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
    public boolean isMap() {
        return true;
    }

    @Override
    public TypeProxy mapKeyType(TypeAndTries typeAndTries) {
        return TypeProxy.create(mapKeyType);
    }

    @Override
    public TypeProxy mapValueType(TypeAndTries typeAndTries) {
        return TypeProxy.create(mapValueType);
    }

    @Override
    public String toSimpleName(TypeAndTries typeAndTries) {
        if (MessageAttributes.class.getName().equals(extendsType)) {
            // We keep the message attributes type simple name to avoid confusion and always display 'MessageAttributes' type.
            return MessageAttributes.class.getSimpleName();

        } else if (isNotBlank(displayName)) {
            return displayName;

        } else {
            String keyTypeSimpleName = mapKeyType(typeAndTries).toSimpleName(typeAndTries);
            String valueTypeSimpleName = mapValueType(typeAndTries).toSimpleName(typeAndTries);
            return String.format(FORMAT_MAP, keyTypeSimpleName, valueTypeSimpleName);
        }
    }

    private class TypeProxyClosureMap extends TypeProxyDefault {

        TypeProxyClosureMap() {
            super(TypeProxyClosureMap.class.getName());
        }

        @Override
        public Trie resolve(TypeAndTries typeAndTries) {
            TypeProxy mapValueTypeProxy = TypeProxy.create(mapValueType);
            TypeProxy mapKeyTypeProxy = TypeProxy.create(mapKeyType);
            return new TypeBuiltInMap.TrieMapClosureArguments(mapKeyTypeProxy, mapValueTypeProxy, typeAndTries);
        }
    }
}
