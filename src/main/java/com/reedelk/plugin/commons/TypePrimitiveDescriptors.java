package com.reedelk.plugin.commons;

import com.reedelk.module.descriptor.model.property.PrimitiveDescriptor;

public class TypePrimitiveDescriptors {

    private TypePrimitiveDescriptors() {
    }

    public static final PrimitiveDescriptor STRING;
    static {
        STRING = new PrimitiveDescriptor();
        STRING.setType(String.class);
    }
}
