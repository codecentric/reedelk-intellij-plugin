package com.reedelk.plugin.component.domain;

import com.reedelk.runtime.api.commons.ScriptUtils;
import com.reedelk.runtime.api.resource.ResourceDynamic;

public class TypeResourceDynamicDescriptor implements TypeDescriptor {

    private static final String DEFAULT_RESOURCE = ScriptUtils.EMPTY_SCRIPT;

    @Override
    public Class<?> type() {
        return ResourceDynamic.class;
    }

    @Override
    public Object defaultValue() {
        return DEFAULT_RESOURCE;
    }
}
