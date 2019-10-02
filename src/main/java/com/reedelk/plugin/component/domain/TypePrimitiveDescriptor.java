package com.reedelk.plugin.component.domain;

import com.google.common.base.Defaults;

import static com.google.common.base.Preconditions.checkArgument;

public class TypePrimitiveDescriptor implements TypeDescriptor {

    private final Class<?> type;

    public TypePrimitiveDescriptor(Class<?> type) {
        checkArgument(type != null, "type");
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
