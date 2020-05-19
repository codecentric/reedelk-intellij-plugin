package com.reedelk.plugin.service.module.impl.component.completion;

public class TypeClosureMapProxy implements TypeProxy {

    private final String mapKeyType;
    private final String mapValueType;

    public TypeClosureMapProxy(String mapKeyType, String mapValueType) {
        this.mapKeyType = mapKeyType;
        this.mapValueType = mapValueType;
    }

    @Override
    public boolean isList(TypeAndTries typeAndTries) {
        return false;
    }

    @Override
    public Trie resolve(TypeAndTries typeAndTries) {
        return new TrieMapClosure(mapKeyType, mapValueType, typeAndTries);
    }

    @Override
    public String toSimpleName(TypeAndTries typeAndTries) {
        String keyTypeSimpleName = TypeUtils.toSimpleName(mapKeyType, typeAndTries);
        String valueTypeSimpleName = TypeUtils.toSimpleName(mapValueType, typeAndTries);
        return String.format("Map<%s,%s>", keyTypeSimpleName, valueTypeSimpleName);
    }

    @Override
    public String getTypeFullyQualifiedName() {
        return TypeClosure.class.getName();
    }

    @Override
    public String listItemType(TypeAndTries typeAndTries) {
        return null;
    }
}
