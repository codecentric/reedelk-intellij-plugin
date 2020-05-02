package com.reedelk.plugin.component.serializer;

import com.reedelk.module.descriptor.model.property.ObjectDescriptor;

public class SerializerUtils {

    private SerializerUtils() {
    }

    public static boolean isTypeObject(Object data) {
        return data instanceof ObjectDescriptor.TypeObject;
    }
}
