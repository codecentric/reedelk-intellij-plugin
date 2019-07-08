package com.esb.plugin.component.domain;

public class TypeScriptDescriptor implements TypeDescriptor {

    public TypeScriptDescriptor() {
    }

    @Override
    public Class<?> type() {
        return TypeScript.class;
    }

    @Override
    public Object defaultValue() {
        return "";
    }

    public static class TypeScript {

    }
}
