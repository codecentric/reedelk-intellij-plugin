package com.reedelk.plugin.component.domain;

public class TypePasswordDescriptor implements TypeDescriptor {

    private static final String defaultPassword = null;

    @Override
    public Class<?> type() {
        return TypePassword.class;
    }

    @Override
    public Object defaultValue() {
        return defaultPassword;
    }

    public interface TypePassword {
    }
}
