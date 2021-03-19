package de.codecentric.reedelk.plugin.service.module.impl.component.completion;

import de.codecentric.reedelk.module.descriptor.model.property.ScriptSignatureArgument;
import de.codecentric.reedelk.module.descriptor.model.type.TypeDescriptor;
import de.codecentric.reedelk.module.descriptor.model.type.TypeFunctionDescriptor;
import de.codecentric.reedelk.module.descriptor.model.type.TypePropertyDescriptor;
import org.jetbrains.annotations.NotNull;

import static de.codecentric.reedelk.runtime.api.commons.Preconditions.checkState;
import static de.codecentric.reedelk.runtime.api.commons.StringUtils.isNotBlank;

public class SuggestionFactory {

    private SuggestionFactory() {
    }

    public static Suggestion copyWithType(@NotNull TypeAndTries allTypesMap,
                                          @NotNull Suggestion suggestion,
                                          @NotNull TypeProxy typeProxy) {
        return Suggestion.create(Suggestion.Type.FUNCTION)
                .returnTypeDisplayValue(typeProxy.toSimpleName(allTypesMap))
                .cursorOffset(suggestion.getCursorOffset())
                .lookupToken(suggestion.getLookupToken())
                .insertValue(suggestion.getInsertValue())
                .tailText(suggestion.getTailText())
                .returnType(typeProxy)
                .build();
    }


    public static Suggestion copyWithTypeAndDisplayName(@NotNull Suggestion suggestion,
                                                    @NotNull ClosureAware.TypeClosureAware typeProxy,
                                                    @NotNull String displayName) {
        return Suggestion.create(Suggestion.Type.FUNCTION)
                .returnTypeDisplayValue(displayName)
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
        return Suggestion.create(Suggestion.Type.GLOBAL)
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
        return Suggestion.create(Suggestion.Type.FUNCTION)
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
        return Suggestion.create(Suggestion.Type.PROPERTY)
                .returnTypeDisplayValue(presentableReturnType)
                .insertValue(descriptor.getName())
                .returnType(propertyType)
                .build();
    }

    static Suggestion create(@NotNull TypeAndTries allTypesMap, @NotNull ScriptSignatureArgument descriptor) {
        String argumentTypeFullyQualifiedName = descriptor.getArgumentType();
        TypeProxy argumentType = TypeProxy.create(argumentTypeFullyQualifiedName);
        String argumentPresentableType = argumentType.toSimpleName(allTypesMap);
        return Suggestion.create(Suggestion.Type.PROPERTY)
                .returnTypeDisplayValue(argumentPresentableType)
                .insertValue(descriptor.getArgumentName())
                .returnType(argumentType)
                .build();
    }
}
