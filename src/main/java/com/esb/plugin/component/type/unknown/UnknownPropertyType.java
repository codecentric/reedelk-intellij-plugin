package com.esb.plugin.component.type.unknown;

import com.esb.plugin.component.domain.TypeDescriptor;

public class UnknownPropertyType implements TypeDescriptor {

    @Override
    public Class<?> type() {
        return UnknownType.class;
    }

    @Override
    public Object defaultValue() {
        throw new UnsupportedOperationException();
    }

    public static class UnknownType {
    }
}
