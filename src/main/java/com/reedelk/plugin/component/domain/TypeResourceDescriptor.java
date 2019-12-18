package com.reedelk.plugin.component.domain;

import com.reedelk.runtime.api.resource.Resource;

public class TypeResourceDescriptor implements TypeDescriptor {

    private static final String DEFAULT_RESOURCE = null;

    @Override
    public Class<?> type() {
        return Resource.class;
    }

    @Override
    public Object defaultValue() {
        return DEFAULT_RESOURCE;
    }
}