package com.reedelk.plugin.component.domain;

public class TypeScriptInlineDescriptor implements TypeDescriptor {

    private final String defaultScript = "";

    @Override
    public Class<?> type() {
        return TypeScriptInline.class;
    }

    @Override
    public Object defaultValue() {
        return defaultScript;
    }

    public static class TypeScriptInline {
    }
}
