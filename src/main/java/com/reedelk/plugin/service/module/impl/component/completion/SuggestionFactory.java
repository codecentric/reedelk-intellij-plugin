package com.reedelk.plugin.service.module.impl.component.completion;

import com.reedelk.module.descriptor.model.property.ScriptSignatureArgument;
import com.reedelk.module.descriptor.model.type.TypeDescriptor;
import com.reedelk.module.descriptor.model.type.TypeFunctionDescriptor;
import com.reedelk.module.descriptor.model.type.TypePropertyDescriptor;
import org.jetbrains.annotations.NotNull;

import static com.reedelk.plugin.service.module.impl.component.completion.Suggestion.Type.*;
import static com.reedelk.runtime.api.commons.Preconditions.checkState;
import static com.reedelk.runtime.api.commons.StringUtils.isNotBlank;

public class SuggestionFactory {

    private SuggestionFactory() {
    }

    public static Suggestion copyWithType(@NotNull TypeAndTries allTypesMap,
                                          @NotNull Suggestion suggestion,
                                          @NotNull TypeProxy typeProxy) {
        return Suggestion.create(FUNCTION)
                .returnTypeDisplayValue(typeProxy.toSimpleName(allTypesMap))
                .cursorOffset(suggestion.getCursorOffset())
                .lookupToken(suggestion.getLookupToken())
                .insertValue(suggestion.getInsertValue())
                .tailText(suggestion.getTailText())
                .returnType(typeProxy)
                .build();
    }

    static Suggestion create(@NotNull TypeAndTries allTypesMap, @NotNull TypeDescriptor typeDescriptor) {
        checkState(typeDescriptor.isGlobal(), "expected global type but it was not (%s).", typeDescriptor.getType());
        String fullyQualifiedTypeName = typeDescriptor.getType();
        TypeProxy returnType = TypeProxy.create(fullyQualifiedTypeName);
        // The global type token is always the simple class name or the display name.
        String globalToken = isNotBlank(typeDescriptor.getDisplayName()) ?
                typeDescriptor.getDisplayName() : returnType.toSimpleName(allTypesMap);
        return Suggestion.create(GLOBAL)
                .returnTypeDisplayValue(globalToken)
                .insertValue(globalToken)
                .lookupToken(globalToken)
                .returnType(returnType)
                .build();
    }

    static Suggestion create(@NotNull TypeAndTries allTypesMap, @NotNull TypeFunctionDescriptor descriptor) {
        String fullyQualifiedTypeName = descriptor.getReturnType();
        TypeProxy returnType = TypeProxy.create(fullyQualifiedTypeName);
        String presentableReturnType = returnType.toSimpleName(allTypesMap);
        return Suggestion.create(FUNCTION)
                .tailText(descriptor.getSignature().substring(descriptor.getName().length())) // We remove from the signature the method name: the tail text will be (String param1, int param2) and so on.
                .returnTypeDisplayValue(presentableReturnType)
                .cursorOffset(descriptor.getCursorOffset())
                .insertValue(descriptor.getName() + "()") // For functions we insert functionName()
                .lookupToken(descriptor.getName())
                .returnType(returnType)
                .build();
    }

    static Suggestion create(@NotNull TypeAndTries allTypesMap, @NotNull TypePropertyDescriptor descriptor) {
        String propertyTypeFullyQualifiedName = descriptor.getType();
        TypeProxy propertyType = TypeProxy.create(propertyTypeFullyQualifiedName);
        String presentableReturnType = propertyType.toSimpleName(allTypesMap);
        return Suggestion.create(PROPERTY)
                .returnTypeDisplayValue(presentableReturnType)
                .insertValue(descriptor.getName())
                .returnType(propertyType)
                .build();
    }

    static Suggestion create(@NotNull TypeAndTries allTypesMap, @NotNull ScriptSignatureArgument descriptor) {
        String argumentTypeFullyQualifiedName = descriptor.getArgumentType();
        TypeProxy argumentType = TypeProxy.create(argumentTypeFullyQualifiedName);
        String argumentPresentableType = argumentType.toSimpleName(allTypesMap);
        return Suggestion.create(PROPERTY)
                .returnTypeDisplayValue(argumentPresentableType)
                .insertValue(descriptor.getArgumentName())
                .returnType(argumentType)
                .build();
    }
}
