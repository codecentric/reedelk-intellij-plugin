package com.reedelk.plugin.component.serializer;

import com.reedelk.module.descriptor.model.TypeObjectDescriptor;

public class SerializerUtils {

    private SerializerUtils() {
    }

    public static boolean isTypeObject(Object data) {
        return data instanceof TypeObjectDescriptor.TypeObject;
    }
}
