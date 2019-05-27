package com.esb.plugin.component.domain;


import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class TypeEnumDescriptor implements TypeDescriptor {

    private final List<String> values;
    private final String defaultValue;

    public TypeEnumDescriptor(final List<String> values, final String defaultValue) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypeEnumDescriptor that = (TypeEnumDescriptor) o;
        return Objects.equals(values, that.values) &&
                Objects.equals(defaultValue, that.defaultValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(values, defaultValue);
    }
}
