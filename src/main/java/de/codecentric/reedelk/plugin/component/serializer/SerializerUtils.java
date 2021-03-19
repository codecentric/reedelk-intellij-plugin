package de.codecentric.reedelk.plugin.component.serializer;

import de.codecentric.reedelk.module.descriptor.model.property.ObjectDescriptor;

public class SerializerUtils {

    private SerializerUtils() {
    }

    public static boolean isTypeObject(Object data) {
        return data instanceof ObjectDescriptor.TypeObject;
    }
}
