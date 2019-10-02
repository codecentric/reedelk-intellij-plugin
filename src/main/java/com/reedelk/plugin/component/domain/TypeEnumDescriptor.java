package com.reedelk.plugin.component.domain;


import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Map;

public class TypeEnumDescriptor implements TypeDescriptor {

    private final Map<String, String> valueAndDisplayNameMap;
    private final String defaultValue;

    public TypeEnumDescriptor(@NotNull Map<String, String> valueAndDisplayNameMap,
                              @NotNull String defaultValue) {
        this.valueAndDisplayNameMap = valueAndDisplayNameMap;
        this.defaultValue = defaultValue;
    }

    @Override
    public Class<?> type() {
        return Enum.class;
    }

    @Override
    public String defaultValue() {
        return defaultValue;
    }

    public Map<String, String> valueAndDisplayMap() {
        return Collections.unmodifiableMap(valueAndDisplayNameMap);
    }
}
