package com.reedelk.plugin.service.module.impl.component.completion;

import com.reedelk.module.descriptor.model.property.ScriptSignatureArgument;
import com.reedelk.module.descriptor.model.type.TypeDescriptor;
import com.reedelk.module.descriptor.model.type.TypeFunctionDescriptor;
import com.reedelk.module.descriptor.model.type.TypePropertyDescriptor;
import com.reedelk.runtime.api.commons.StringUtils;
import org.jetbrains.annotations.NotNull;

import static com.reedelk.plugin.service.module.impl.component.completion.Suggestion.Type.*;
import static com.reedelk.plugin.service.module.impl.component.completion.TypeUtils.returnTypeOrDefault;
import static com.reedelk.plugin.service.module.impl.component.completion.TypeUtils.toSimpleName;
import static com.reedelk.runtime.api.commons.Preconditions.checkState;

public class SuggestionFactory {

    private SuggestionFactory() {
    }

    static Suggestion create(@NotNull TypeDescriptor typeDescriptor) {
        checkState(typeDescriptor.isGlobal(), "expected global type but it was not (%s).", typeDescriptor.getType());
        String returnType = returnTypeOrDefault(typeDescriptor.getType());
        // The global type token is always the simple class name or the display
        // name if it was bound to the script engine with a different name. This also applies
        // if the type would be a list with item type, it would still be displayed with the display name
        // or the simple class name.
        String globalToken = StringUtils.isNotBlank(typeDescriptor.getDisplayName()) ?
                typeDescriptor.getDisplayName() :
                TypeUtils.toSimpleName(typeDescriptor.getType());
        return Suggestion.create(GLOBAL)
                .returnTypeDisplayValue(globalToken)
                .insertValue(globalToken)
                .lookupToken(globalToken)
                .returnType(returnType)
                .build();
    }

    static Suggestion create(@NotNull TypeAndTries allTypesMap, @NotNull TypeFunctionDescriptor descriptor) {
        String returnType = returnTypeOrDefault(descriptor.getReturnType());
        String presentableReturnType = toSimpleName(returnType, allTypesMap);
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
        String propertyType = returnTypeOrDefault(descriptor.getType());
        String presentableReturnType = toSimpleName(propertyType, allTypesMap);
        return Suggestion.create(PROPERTY)
                .returnTypeDisplayValue(presentableReturnType)
                .insertValue(descriptor.getName())
                .returnType(propertyType)
                .build();
    }

    static Suggestion create(@NotNull TypeAndTries allTypesMap, @NotNull ScriptSignatureArgument descriptor) {
        String argumentType = returnTypeOrDefault(descriptor.getArgumentType());
        String argumentPresentableType = toSimpleName(argumentType, allTypesMap);
        return Suggestion.create(PROPERTY)
                .returnTypeDisplayValue(argumentPresentableType)
                .insertValue(descriptor.getArgumentName())
                .returnType(argumentType)
                .build();
    }
}
