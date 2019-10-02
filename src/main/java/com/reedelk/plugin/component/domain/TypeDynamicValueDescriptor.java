package com.reedelk.plugin.component.domain;

import com.reedelk.runtime.api.script.dynamicvalue.DynamicValue;

public class TypeDynamicValueDescriptor<T extends DynamicValue> implements TypeDescriptor {

    private static final String defaultValue = null;
    private final Class<T> typeClazz;

    public TypeDynamicValueDescriptor(Class<T> typeClazz) {
        this.typeClazz = typeClazz;
    }

    @Override
    public Class<?> type() {
        return typeClazz;
    }

    @Override
    public Object defaultValue() {
        return defaultValue;
    }

}
