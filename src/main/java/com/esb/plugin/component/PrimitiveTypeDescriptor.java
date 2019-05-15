package com.esb.plugin.component;

public class PrimitiveTypeDescriptor implements PropertyTypeDescriptor {

    private final Class<?> type;

    public PrimitiveTypeDescriptor(Class<?> type) {
        this.type = type;
    }

    @Override
    public Class<?> type() {
        return type;
    }
}
