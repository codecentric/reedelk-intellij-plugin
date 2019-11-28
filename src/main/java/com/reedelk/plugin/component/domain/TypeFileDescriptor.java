package com.reedelk.plugin.component.domain;

public class TypeFileDescriptor implements TypeDescriptor {

    private static final String DEFAULT_FILE = null;

    @Override
    public Class<?> type() {
        return TypeFile.class;
    }

    @Override
    public Object defaultValue() {
        return DEFAULT_FILE;
    }

    public interface TypeFile {
    }
}
