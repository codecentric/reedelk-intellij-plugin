package com.reedelk.plugin.commons;

import com.reedelk.plugin.component.domain.Shared;
import com.reedelk.plugin.component.domain.TypeObjectDescriptor;
import com.reedelk.runtime.commons.JsonParser;

import static com.reedelk.plugin.component.domain.Shared.NO;

public class TypeObjectFactory {

    private TypeObjectFactory() {
    }

    public static TypeObjectDescriptor.TypeObject newInstance() {
        return TypeObjectDescriptor.newInstance();
    }

    public static TypeObjectDescriptor.TypeObject newInstance(TypeObjectDescriptor descriptor) {
        Shared shared = descriptor.getShared();
        String typeFullyQualifiedName = descriptor.getTypeFullyQualifiedName();
        TypeObjectDescriptor.TypeObject typeObject = TypeObjectDescriptor.newInstance();
        if (NO.equals(shared)) {
            // If the type object is not shared, then the serialized
            // json contains the fully qualified name.
            typeObject.set(JsonParser.Implementor.name(), typeFullyQualifiedName);
        }
        return typeObject;
    }
}
