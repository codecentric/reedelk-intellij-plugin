package com.reedelk.plugin.service.module.impl.component.completion;

import static com.reedelk.plugin.service.module.impl.component.completion.Suggestion.Type.PROPERTY;

class TypeProxyClosureMap implements TypeProxy {

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
