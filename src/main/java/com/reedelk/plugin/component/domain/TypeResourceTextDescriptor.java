package com.reedelk.plugin.component.domain;

import com.reedelk.runtime.api.resource.ResourceText;

public class TypeResourceTextDescriptor implements TypeDescriptor {

    private static final String DEFAULT_RESOURCE = null;

    @Override
    public Class<?> type() {
        return ResourceText.class;
    }

    @Override
    public Object defaultValue() {
        return DEFAULT_RESOURCE;
    }
}