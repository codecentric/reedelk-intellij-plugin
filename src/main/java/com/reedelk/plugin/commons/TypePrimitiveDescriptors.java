package com.reedelk.plugin.commons;

import com.reedelk.module.descriptor.model.TypePrimitiveDescriptor;

public class TypePrimitiveDescriptors {

    private TypePrimitiveDescriptors() {
    }

    public static final TypePrimitiveDescriptor STRING;
    static {
        STRING = new TypePrimitiveDescriptor();
        STRING.setType(String.class);
    }
}
