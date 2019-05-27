package com.esb.plugin.component.domain;

import com.google.common.base.Defaults;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypePrimitiveDescriptor that = (TypePrimitiveDescriptor) o;
        return type.equals(that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }
}
