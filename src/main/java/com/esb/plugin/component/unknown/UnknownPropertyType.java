package com.esb.plugin.component.unknown;

import com.esb.plugin.component.TypeDescriptor;

public class UnknownPropertyType implements TypeDescriptor {

    @Override
    public Class<?> type() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object defaultValue() {
        throw new UnsupportedOperationException();
    }

}
