package com.reedelk.plugin.component.domain;

import com.reedelk.runtime.api.script.Script;

public class TypeScriptDescriptor implements TypeDescriptor {

    private static final String DEFAULT_SCRIPT = null;

    @Override
    public Class<?> type() {
        return Script.class;
    }

    @Override
    public Object defaultValue() {
        return DEFAULT_SCRIPT;
    }

}
