package de.codecentric.reedelk.plugin.service.module.impl.component.completion;

import de.codecentric.reedelk.plugin.service.module.impl.component.completion.Suggestion;
import de.codecentric.reedelk.plugin.service.module.impl.component.completion.TypeAndTries;
import de.codecentric.reedelk.plugin.service.module.impl.component.completion.TypeProxy;
import de.codecentric.reedelk.module.descriptor.model.property.ScriptSignatureArgument;
import de.codecentric.reedelk.module.descriptor.model.type.TypeDescriptor;
import de.codecentric.reedelk.module.descriptor.model.type.TypeFunctionDescriptor;
import de.codecentric.reedelk.module.descriptor.model.type.TypePropertyDescriptor;

import java.util.List;

import static de.codecentric.reedelk.plugin.service.module.impl.component.completion.Suggestion.Type.FUNCTION;
import static de.codecentric.reedelk.plugin.service.module.impl.component.completion.Suggestion.Type.PROPERTY;

public class SuggestionTestUtils {

    public static Suggestion createFunctionSuggestion(String insertValue, String returnType, TypeAndTries typeAndTries) {
        TypeProxy typeProxy = TypeProxy.create(returnType);
        return Suggestion.create(FUNCTION)
                .returnTypeDisplayValue(typeProxy.toSimpleName(typeAndTries))
                .returnType(typeProxy)
                .insertValue(insertValue)
                .build();
    }

    public static Suggestion createPropertySuggestion(String insertValue, String returnType, TypeAndTries typeAndTries) {
        TypeProxy typeProxy = TypeProxy.create(returnType);
        return Suggestion.create(PROPERTY)
                .returnTypeDisplayValue(typeProxy.toSimpleName(typeAndTries))
                .insertValue(insertValue)
                .returnType(typeProxy)
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
