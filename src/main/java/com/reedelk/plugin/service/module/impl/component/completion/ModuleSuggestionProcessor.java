package com.reedelk.plugin.service.module.impl.component.completion;

import com.intellij.openapi.diagnostic.Logger;
import com.reedelk.module.descriptor.model.ModuleDescriptor;
import com.reedelk.module.descriptor.model.property.ObjectDescriptor;
import com.reedelk.module.descriptor.model.property.PropertyDescriptor;
import com.reedelk.module.descriptor.model.property.ScriptSignatureDescriptor;
import com.reedelk.module.descriptor.model.type.TypeDescriptor;
import com.reedelk.plugin.editor.properties.context.ComponentPropertyPath;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static com.reedelk.plugin.service.module.impl.component.completion.Suggestion.Type.FUNCTION;
import static com.reedelk.plugin.service.module.impl.component.completion.Suggestion.Type.PROPERTY;
import static java.lang.String.format;

public class ModuleSuggestionProcessor {

    private static final Logger LOG = Logger.getInstance(ModuleSuggestionProcessor.class);

    private final Trie moduleGlobalTypes;
    private final TrieMapWrapper allTypesMap; // Only used for lookup.
    private final Map<String, Trie> moduleTypes;
    private final Map<String, Trie> moduleSignatureTypes;

    public ModuleSuggestionProcessor(@NotNull TrieMapWrapper allTypesMap,
                                     @NotNull Trie moduleGlobalTypes,
                                     @NotNull Map<String, Trie> moduleTypes,
                                     @NotNull Map<String, Trie> moduleSignatureTypes) {
        this.allTypesMap = allTypesMap;
        this.moduleTypes = moduleTypes;
        this.moduleGlobalTypes = moduleGlobalTypes;
        this.moduleSignatureTypes = moduleSignatureTypes;
    }

    public void populate(@NotNull ModuleDescriptor moduleDescriptor) {
        // Defined module types
        // First create all the tries for the type.
        
        moduleDescriptor.getTypes().forEach(type -> {
            try {
                populate(type);
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

    private void populate(TypeDescriptor typeDescriptor) {
        // Global root type
        if (typeDescriptor.isGlobal()) {
            Suggestion globalTypeProperty = Suggestion.create(PROPERTY)
                    .lookup(typeDescriptor.getDisplayName())
                    .returnType(typeDescriptor.getType())
                    .build();
            moduleGlobalTypes.insert(globalTypeProperty);
        }

        // We first must register all the tries and then the functions. Because one function
        // might depend on a trie of another type previously defined in the same module.
        Trie typeTrie = new TrieDefault(typeDescriptor.getExtendsType(), typeDescriptor.getListItemType());
        moduleTypes.put(typeDescriptor.getType(), typeTrie);

        // Functions for the type
        typeDescriptor.getFunctions().forEach(typeFunctionDescriptor -> {
            Suggestion functionSuggestion = Suggestion.create(FUNCTION)
                    .lookup(typeFunctionDescriptor.getName() + "()")
                    .lookupDisplayValue(typeFunctionDescriptor.getName())
                    // We remove from the signature the method name: the tail text will be (String param1, int param2) and so on.
                    .tailText(typeFunctionDescriptor.getSignature().substring(typeFunctionDescriptor.getName().length()))
                    .cursorOffset(typeFunctionDescriptor.getCursorOffset())
                    .returnType(typeFunctionDescriptor.getReturnType())
                    .returnType(PresentableTypeUtils.from(typeFunctionDescriptor.getReturnType()))
                    .build();
            typeTrie.insert(functionSuggestion);
        });

        // Properties for the type
        typeDescriptor.getProperties().forEach(typePropertyDescriptor -> {
            Suggestion propertySuggestion = Suggestion.create(PROPERTY)
                    .lookup(typePropertyDescriptor.getName())
                    .returnType(typePropertyDescriptor.getType())
                    .build();
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
        Trie trie = new TrieDefault();
        descriptor.getArguments().stream().map(argument -> {
            // Lookup the type: we want to make it nice to present in the autocompletion: List<Message> for example.
            String argumentType = argument.getArgumentType();
            Trie typeTrie = allTypesMap.getOrDefault(argumentType, Default.UNKNOWN);
            String argumentPresentableType = PresentableTypeUtils.from(argumentType, typeTrie);
            return Suggestion.create(PROPERTY)
                    .returnType(argument.getArgumentType())
                    .returnTypeDisplayValue(argumentPresentableType)
                    .lookup(argument.getArgumentName())
                    .build();
        }).forEach(trie::insert);
        return trie;
    }
}
