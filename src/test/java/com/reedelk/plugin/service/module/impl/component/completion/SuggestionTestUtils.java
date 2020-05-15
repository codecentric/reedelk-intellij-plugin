package com.reedelk.plugin.service.module.impl.component.completion;

import com.reedelk.module.descriptor.model.property.ScriptSignatureArgument;
import com.reedelk.module.descriptor.model.type.TypeDescriptor;
import com.reedelk.module.descriptor.model.type.TypeFunctionDescriptor;
import com.reedelk.module.descriptor.model.type.TypePropertyDescriptor;

import java.util.List;

public class SuggestionTestUtils {

    public static Suggestion createFunctionSuggestion(String insertValue, String returnType) {
        return Suggestion.create(Suggestion.Type.FUNCTION)
                .insertValue(insertValue)
                .returnType(returnType)
                .build();
    }

    public static Suggestion createPropertySuggestion(String insertValue, String returnType) {
        return Suggestion.create(Suggestion.Type.PROPERTY)
                .insertValue(insertValue)
                .returnType(returnType)
                .build();
    }

    public static TypePropertyDescriptor createPropertyDescriptor(String name, String type) {
        TypePropertyDescriptor descriptor = new TypePropertyDescriptor();
        descriptor.setType(type);
        descriptor.setName(name);
        return descriptor;
    }

    public static TypePropertyDescriptor createStringPropertyDescriptor(String name) {
        TypePropertyDescriptor descriptor = new TypePropertyDescriptor();
        descriptor.setType(String.class.getName());
        descriptor.setName(name);
        return descriptor;
    }

    public static TypePropertyDescriptor createIntPropertyDescriptor(String name) {
        TypePropertyDescriptor descriptor = new TypePropertyDescriptor();
        descriptor.setType(int.class.getName());
        descriptor.setName(name);
        return descriptor;
    }

    public static TypeFunctionDescriptor createFunctionDescriptor(String name, String signature, String returnType) {
        return createFunctionDescriptor(name, signature, returnType, 0);
    }

    public static TypeFunctionDescriptor createFunctionDescriptor(String name, String signature, String returnType, int cursorOffset) {
        TypeFunctionDescriptor descriptor = new TypeFunctionDescriptor();
        descriptor.setCursorOffset(cursorOffset);
        descriptor.setReturnType(returnType);
        descriptor.setSignature(signature);
        descriptor.setName(name);
        return descriptor;
    }

    public static TypeDescriptor createTypeDescriptor(String type, List<TypePropertyDescriptor> properties, List<TypeFunctionDescriptor> functions) {
        TypeDescriptor descriptor = new TypeDescriptor();
        descriptor.setProperties(properties);
        descriptor.setFunctions(functions);
        descriptor.setType(type);
        return descriptor;
    }

    public static ScriptSignatureArgument createScriptSignatureArgument(String argumentName, String argumentType) {
        return new ScriptSignatureArgument(argumentName, argumentType);
    }
}
