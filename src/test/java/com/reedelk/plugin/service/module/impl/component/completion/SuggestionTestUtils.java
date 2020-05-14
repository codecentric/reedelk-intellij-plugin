package com.reedelk.plugin.service.module.impl.component.completion;

import com.reedelk.module.descriptor.model.type.TypeDescriptor;
import com.reedelk.module.descriptor.model.type.TypeFunctionDescriptor;
import com.reedelk.module.descriptor.model.type.TypePropertyDescriptor;

import java.util.List;

public class SuggestionTestUtils {

    public static Suggestion createFunction(String lookup, String returnType) {
        return Suggestion.create(Suggestion.Type.FUNCTION)
                .returnType(returnType)
                .insertValue(lookup)
                .build();
    }

    public static Suggestion createProperty(String lookup, String returnType) {
        return Suggestion.create(Suggestion.Type.PROPERTY)
                .returnType(returnType)
                .insertValue(lookup)
                .build();
    }

    public static TypePropertyDescriptor createStringProperty(String name) {
        TypePropertyDescriptor idProperty = new TypePropertyDescriptor();
        idProperty.setType(String.class.getName());
        idProperty.setName(name);
        return idProperty;
    }

    public static TypePropertyDescriptor createIntProperty(String name) {
        TypePropertyDescriptor idProperty = new TypePropertyDescriptor();
        idProperty.setType(int.class.getName());
        idProperty.setName(name);
        return idProperty;
    }

    public static TypeFunctionDescriptor createMethod(String name, String signature, String returnType, int cursorOffset) {
        TypeFunctionDescriptor method = new TypeFunctionDescriptor();
        method.setCursorOffset(cursorOffset);
        method.setReturnType(returnType);
        method.setSignature(signature);
        method.setName(name);
        return method;
    }

    public static TypeFunctionDescriptor createMethod(String name, String signature, String returnType) {
        return createMethod(name, signature, returnType, 0);
    }

    public static TypeDescriptor createType(String type, List<TypePropertyDescriptor> properties, List<TypeFunctionDescriptor> functions) {
        TypeDescriptor typeDescriptor = new TypeDescriptor();
        typeDescriptor.setProperties(properties);
        typeDescriptor.setFunctions(functions);
        typeDescriptor.setType(type);
        return typeDescriptor;
    }
}
