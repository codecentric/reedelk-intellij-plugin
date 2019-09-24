package com.reedelk.plugin.component.domain;

import com.reedelk.runtime.api.script.DynamicValue;

public class TypeDynamicValueDescriptor implements TypeDescriptor {

    private final String defaultValue = "";

    @Override
    public Class<?> type() {
        return DynamicValue.class;
    }

    @Override
    public Object defaultValue() {
        return defaultValue;
    }

}
