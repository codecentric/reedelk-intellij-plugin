package com.reedelk.plugin.component.domain;

import com.reedelk.runtime.api.resource.ResourceBinary;

public class TypeResourceBinaryDescriptor implements TypeDescriptor {

    private static final String DEFAULT_RESOURCE = null;

    @Override
    public Class<?> type() {
        return ResourceBinary.class;
    }

    @Override
    public Object defaultValue() {
        return DEFAULT_RESOURCE;
    }
}
