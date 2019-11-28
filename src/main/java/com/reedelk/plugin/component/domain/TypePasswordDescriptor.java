package com.reedelk.plugin.component.domain;

public class TypePasswordDescriptor implements TypeDescriptor {

    private static final String DEFAULT_PASSWORD = null;

    @Override
    public Class<?> type() {
        return TypePassword.class;
    }

    @Override
    public Object defaultValue() {
        return DEFAULT_PASSWORD;
    }

    public interface TypePassword {
    }
}
