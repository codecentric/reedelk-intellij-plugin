package com.reedelk.plugin.service.module.impl.component.completion;

import com.intellij.openapi.diagnostic.Logger;
import com.reedelk.module.descriptor.model.ModuleDescriptor;
import com.reedelk.module.descriptor.model.property.ObjectDescriptor;
import com.reedelk.module.descriptor.model.property.PropertyDescriptor;
import com.reedelk.module.descriptor.model.property.ScriptSignatureArgument;
import com.reedelk.module.descriptor.model.property.ScriptSignatureDescriptor;
import com.reedelk.module.descriptor.model.type.TypeDescriptor;
import com.reedelk.module.descriptor.model.type.TypeFunctionDescriptor;
import com.reedelk.module.descriptor.model.type.TypePropertyDescriptor;
import com.reedelk.plugin.editor.properties.context.ComponentPropertyPath;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static com.reedelk.plugin.service.module.impl.component.completion.Suggestion.Type.*;
import static java.lang.String.format;

public class ModuleSuggestionProcessor {

    private static final Logger LOG = Logger.getInstance(ModuleSuggestionProcessor.class);

    private final Trie moduleGlobalTypes;
    private final TypeAndTries allTypesMap; // Only used for lookup.
    private final Map<String, Trie> moduleTypes;
    private final Map<String, Trie> moduleSignatureTypes;

    public ModuleSuggestionProcessor(@NotNull TypeAndTries allTypesMap,
                                     @NotNull Trie moduleGlobalTypes,
                                     @NotNull Map<String, Trie> moduleTypes,
                                     @NotNull Map<String, Trie> moduleSignatureTypes) {
        this.allTypesMap = allTypesMap;
        this.moduleTypes = moduleTypes;
        this.moduleGlobalTypes = moduleGlobalTypes;
        this.moduleSignatureTypes = moduleSignatureTypes;
    }

    public void populate(@NotNull ModuleDescriptor moduleDescriptor) {

        // First create all the tries for the type.
        moduleDescriptor.getTypes().forEach(typeDescriptor -> {
            // We first must register all the tries and then the functions. Because one function
            // might depend on a trie of another type previously defined in the same module.
            Trie typeTrie = new TrieImpl(typeDescriptor.getExtendsType(), typeDescriptor.getListItemType());
            moduleTypes.put(typeDescriptor.getType(), typeTrie);
        });

        // Then populate Global type, functions and properties for each type.
        moduleDescriptor.getTypes().forEach(typeDescriptor -> {
            try {
                Trie typeTrie = moduleTypes.get(typeDescriptor.getType());
                populate(typeDescriptor, typeTrie);
            } catch (Exception exception) {
                String errorMessage = format("Could not populate types for module descriptor=[%s], cause=[%s]", moduleDescriptor.getName(), exception.getMessage());
                LOG.warn(errorMessage, exception);
            }
        });

        // Signatures by component's properties
        moduleDescriptor.getComponents().forEach(componentDescriptor -> {
            componentDescriptor.getProperties().forEach(propertyDescriptor -> {
                String parent = componentDescriptor.getFullyQualifiedName();
                populate(propertyDescriptor, parent);
            });
        });
    }

    private void populate(TypeDescriptor typeDescriptor, Trie typeTrie) {
        // Global root type
        if (typeDescriptor.isGlobal()) {
            Suggestion globalTypeSuggestion = createGlobal(typeDescriptor);
            moduleGlobalTypes.insert(globalTypeSuggestion);
        }

        // Functions for the type
        typeDescriptor.getFunctions().forEach(typeFunctionDescriptor -> {
            Suggestion functionSuggestion = createFunction(allTypesMap, typeFunctionDescriptor);
            typeTrie.insert(functionSuggestion);
        });

        // Properties for the type
        typeDescriptor.getProperties().forEach(typePropertyDescriptor -> {
            Suggestion propertySuggestion = createProperty(allTypesMap, typePropertyDescriptor);
            typeTrie.insert(propertySuggestion);
        });
    }

    private void populate(PropertyDescriptor propertyDescriptor, String parentComponentPropertyPath) {
        if (propertyDescriptor.getType() instanceof ObjectDescriptor) {
            // If the property type is TypeObject, we must recursively add the suggestions for each nested property.
            String name = propertyDescriptor.getName();
            String componentPropertyPath = ComponentPropertyPath.join(parentComponentPropertyPath, name);
            ObjectDescriptor typeObjectDescriptor = propertyDescriptor.getType();
            typeObjectDescriptor.getObjectProperties()
                    .forEach(subProperty -> populate(subProperty, componentPropertyPath));

        } else {
            String name = propertyDescriptor.getName();
            String componentPropertyPath = ComponentPropertyPath.join(parentComponentPropertyPath, name);
            ScriptSignatureDescriptor scriptSignature = propertyDescriptor.getScriptSignature();
            if (scriptSignature != null) {
                Trie trie = createTrieFromScriptSignature(scriptSignature);
                moduleSignatureTypes.put(componentPropertyPath, trie);
            }
        }
    }

    private Trie createTrieFromScriptSignature(@NotNull ScriptSignatureDescriptor descriptor) {
        Trie trie = new TrieImpl();
        descriptor.getArguments()
                .stream()
                .map(scriptSignatureArgument -> createProperty(allTypesMap, scriptSignatureArgument))
                .forEach(trie::insert);
        return trie;
    }

    static Suggestion createGlobal(@NotNull TypeDescriptor typeDescriptor) {
        return Suggestion.create(GLOBAL)
                .insertValue(typeDescriptor.getDisplayName())
                .returnType(typeDescriptor.getType())
                .returnTypeDisplayValue(PresentableTypeUtils.from(typeDescriptor.getDisplayName()))
                .build();
    }

    static Suggestion createFunction(@NotNull TypeAndTries allTypesMap, @NotNull TypeFunctionDescriptor descriptor) {
        String returnType = descriptor.getReturnType();
        String presentableReturnType = returnTypeDisplayValueFrom(allTypesMap, returnType);
        return Suggestion.create(FUNCTION)
                .tailText(descriptor.getSignature().substring(descriptor.getName().length())) // We remove from the signature the method name: the tail text will be (String param1, int param2) and so on.
                .returnTypeDisplayValue(presentableReturnType)
                .cursorOffset(descriptor.getCursorOffset())
                .insertValue(descriptor.getName() + "()") // For functions we insert functionName()
                .lookupToken(descriptor.getName())
                .returnType(returnType)
                .build();
    }

    static Suggestion createProperty(@NotNull TypeAndTries allTypesMap, @NotNull TypePropertyDescriptor descriptor) {
        String propertyType = descriptor.getType();
        String presentableReturnType = returnTypeDisplayValueFrom(allTypesMap, propertyType);
        return Suggestion.create(PROPERTY)
                .returnTypeDisplayValue(presentableReturnType)
                .insertValue(descriptor.getName())
                .returnType(propertyType)
                .build();
    }

    static Suggestion createProperty(@NotNull TypeAndTries allTypesMap, @NotNull ScriptSignatureArgument descriptor) {
        String argumentType = descriptor.getArgumentType();
        String argumentPresentableType = returnTypeDisplayValueFrom(allTypesMap, argumentType);
        return Suggestion.create(PROPERTY)
                .returnTypeDisplayValue(argumentPresentableType)
                .insertValue(descriptor.getArgumentName())
                .returnType(argumentType)
                .build();
    }

    private static String returnTypeDisplayValueFrom(@NotNull TypeAndTries allTypesMap, @NotNull String type) {
        Trie typeTrie = allTypesMap.getOrDefault(type, Default.UNKNOWN);
        return PresentableTypeUtils.from(type, typeTrie);
    }
}
