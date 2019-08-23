package com.reedelk.plugin.component.domain;

import java.util.Map;
import java.util.Optional;

public class TypeMapDescriptor implements TypeDescriptor {

    private final String tabGroup;

    public TypeMapDescriptor(String tabGroup) {
        this.tabGroup = tabGroup;
    }

    @Override
    public Class<?> type() {
        return Map.class;
    }

    @Override
    public Object defaultValue() {
        return null;
    }

    public Optional<String> getTabGroup() {
        return Optional.ofNullable(tabGroup);
    }
}
