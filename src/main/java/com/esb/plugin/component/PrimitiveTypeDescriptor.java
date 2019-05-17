package com.esb.plugin.component;

import com.google.common.base.Defaults;

public class PrimitiveTypeDescriptor implements TypeDescriptor {

    private final Class<?> type;

    public PrimitiveTypeDescriptor(Class<?> type) {
        this.type = type;
    }

    @Override
    public Class<?> type() {
        return type;
    }

    @Override
    public Object defaultValue() {
        return Defaults.defaultValue(type);
    }
}
