package com.reedelk.plugin.component.domain;

public class TypeScriptDescriptor implements TypeDescriptor {

    public static final String INLINE_ANNOTATION_PARAM_NAME = "inline";

    private final String defaultScript = "";
    private final boolean inline;

    public TypeScriptDescriptor(final boolean inline) {
        this.inline = inline;
    }

    @Override
    public Class<?> type() {
        return TypeScript.class;
    }

    @Override
    public Object defaultValue() {
        return defaultScript;
    }

    public boolean isInline() {
        return inline;
    }

    public static class TypeScript {

    }
}
