package com.esb.plugin.component.domain;


import java.util.Collections;
import java.util.List;

public class EnumTypeDescriptor implements TypeDescriptor {

    private final List<String> values;
    private final String defaultValue;

    public EnumTypeDescriptor(final List<String> values, final String defaultValue) {
        this.defaultValue = defaultValue;
        this.values = values;
    }

    @Override
    public Class<?> type() {
        return Enum.class;
    }

    @Override
    public Object defaultValue() {
        return defaultValue;
    }

    public List<String> possibleValues() {
        return Collections.unmodifiableList(values);
    }
}