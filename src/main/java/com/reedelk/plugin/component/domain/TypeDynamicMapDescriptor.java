package com.reedelk.plugin.component.domain;

public class TypeDynamicMapDescriptor<T> extends TypeMapDescriptor {

    private final Class<T> typeClazz;

    public TypeDynamicMapDescriptor(Class<T> typeClazz, String tabGroup) {
        super(tabGroup);
        this.typeClazz = typeClazz;
    }

    @Override
    public Class<?> type() {
        return typeClazz;
    }
}
