package com.reedelk.plugin.component.type.unknown;

import com.reedelk.plugin.component.domain.TypeDescriptor;

public class UnknownPropertyType implements TypeDescriptor {

    @Override
    public Class<?> getType() {
        return UnknownType.class;
    }

    public interface UnknownType {
    }
}
