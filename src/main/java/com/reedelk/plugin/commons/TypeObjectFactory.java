package com.reedelk.plugin.commons;

import com.reedelk.module.descriptor.model.property.ObjectDescriptor;
import com.reedelk.module.descriptor.model.property.Shared;
import com.reedelk.runtime.commons.JsonParser;

public class TypeObjectFactory {

    private TypeObjectFactory() {
    }

    public static ObjectDescriptor.TypeObject newInstance() {
        return ObjectDescriptor.newInstance();
    }

    public static ObjectDescriptor.TypeObject newInstance(ObjectDescriptor descriptor) {
        String typeFullyQualifiedName = descriptor.getTypeFullyQualifiedName();
        ObjectDescriptor.TypeObject typeObject = ObjectDescriptor.newInstance();

        if (Shared.NO.equals(descriptor.getShared())) {
            // If the type object is not shared, then the serialized
            // json contains the fully qualified name.
            typeObject.set(JsonParser.Implementor.name(), typeFullyQualifiedName);
        }
        return typeObject;
    }
}
