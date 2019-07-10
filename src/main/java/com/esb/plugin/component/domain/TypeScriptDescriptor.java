package com.esb.plugin.component.domain;

public class TypeScriptDescriptor implements TypeDescriptor {

    private final String defaultScript = "";

    @Override
    public Class<?> type() {
        return TypeScript.class;
    }

    @Override
    public Object defaultValue() {
        return defaultScript;
    }

    public static class TypeScript {

    }
}
