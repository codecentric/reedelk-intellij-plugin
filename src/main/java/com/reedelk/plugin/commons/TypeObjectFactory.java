package com.reedelk.plugin.commons;

import com.reedelk.component.descriptor.Shared;
import com.reedelk.component.descriptor.TypeObjectDescriptor;
import com.reedelk.runtime.commons.JsonParser;

public class TypeObjectFactory {

    private TypeObjectFactory() {
    }

    public static TypeObjectDescriptor.TypeObject newInstance() {
        return TypeObjectDescriptor.newInstance();
    }

    public static TypeObjectDescriptor.TypeObject newInstance(TypeObjectDescriptor descriptor) {
        String typeFullyQualifiedName = descriptor.getTypeFullyQualifiedName();
        TypeObjectDescriptor.TypeObject typeObject = TypeObjectDescriptor.newInstance();

        if (Shared.NO.equals(descriptor.getShared())) {
            // If the type object is not shared, then the serialized
            // json contains the fully qualified name.
            typeObject.set(JsonParser.Implementor.name(), typeFullyQualifiedName);
        }
        return typeObject;
    }
}
