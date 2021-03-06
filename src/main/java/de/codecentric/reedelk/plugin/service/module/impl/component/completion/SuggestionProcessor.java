package de.codecentric.reedelk.plugin.service.module.impl.component.completion;

import de.codecentric.reedelk.plugin.editor.properties.context.ComponentPropertyPath;
import com.intellij.openapi.diagnostic.Logger;
import de.codecentric.reedelk.module.descriptor.model.ModuleDescriptor;
import de.codecentric.reedelk.module.descriptor.model.property.ObjectDescriptor;
import de.codecentric.reedelk.module.descriptor.model.property.PropertyDescriptor;
import de.codecentric.reedelk.module.descriptor.model.property.ScriptSignatureDescriptor;
import de.codecentric.reedelk.module.descriptor.model.type.TypeDescriptor;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static de.codecentric.reedelk.plugin.message.ReedelkBundle.message;
import static de.codecentric.reedelk.runtime.api.commons.StringUtils.isNotBlank;

public class SuggestionProcessor {

    private static final Logger LOG = Logger.getInstance(SuggestionProcessor.class);

    private final Trie moduleGlobalTypes;
    private final TypeAndTries allTypesMap; // Only used for lookup, never updated.
    private final Map<String, Trie> moduleTypes;
    private final Map<String, Trie> moduleSignatureTypes;

    public SuggestionProcessor(@NotNull TypeAndTries allTypesMap,
                               @NotNull Trie moduleGlobalTypes,
                               @NotNull Map<String, Trie> moduleTypes,
                               @NotNull Map<String, Trie> moduleSignatureTypes) {
        this.allTypesMap = allTypesMap;
        this.moduleTypes = moduleTypes;
        this.moduleGlobalTypes = moduleGlobalTypes;
        this.moduleSignatureTypes = moduleSignatureTypes;
    }

    public void populate(@NotNull ModuleDescriptor moduleDescriptor) {

        moduleDescriptor.getTypes().forEach(typeDescriptor -> {
            // We first must register all the tries and then the functions. Because one function
            // might depend on a trie of another type previously defined in the same module.
            String typeFullyQualifiedName = typeDescriptor.getType();
            String listItemType = typeDescriptor.getListItemType();
            String mapValueType = typeDescriptor.getMapValueType();
            String mapKeyType = typeDescriptor.getMapKeyType();
            String extendsType = typeDescriptor.getExtendsType();
            String displayName = typeDescriptor.getDisplayName();
            Trie typeTrie;
            if (isNotBlank(listItemType)) {
                typeTrie = new TrieList(typeFullyQualifiedName, extendsType, displayName, listItemType);
            } else if (isNotBlank(mapValueType)) {
                typeTrie = new TrieMap(typeFullyQualifiedName, extendsType, displayName, mapKeyType, mapValueType);
            } else {
                typeTrie = new TrieDefault(typeFullyQualifiedName, extendsType, displayName);
            }
            moduleTypes.put(typeFullyQualifiedName, typeTrie);
        });

        // Then populate global type, functions and properties for each type.
        moduleDescriptor.getTypes().forEach(typeDescriptor -> {
            try {
                Trie typeTrie = moduleTypes.get(typeDescriptor.getType());
                populate(typeDescriptor, typeTrie);
            } catch (Exception exception) {
                String errorMessage = message("module.completion.suggestion.processor.error",
                        typeDescriptor.getType(), moduleDescriptor.getName(), exception.getMessage());
                LOG.warn(errorMessage, exception);
            }
        });

        // Signatures by component's properties
        moduleDescriptor.getComponents().forEach(componentDescriptor ->
                componentDescriptor.getProperties().forEach(propertyDescriptor -> {
                    String parent = componentDescriptor.getFullyQualifiedName();
                    populate(propertyDescriptor, parent);
                }));
    }

    // For each type we must:
    // 1. Add suggestion to the global types trie if it is a global type.
    // 2. Add a suggestion for each function defined in the type.
    // 3. Add a suggestion for each property defined in the type.
    private void populate(TypeDescriptor typeDescriptor, Trie typeTrie) {
        // Global root type
        if (typeDescriptor.isGlobal()) {
            Suggestion globalTypeSuggestion = SuggestionFactory.create(allTypesMap, typeDescriptor);
            moduleGlobalTypes.insert(globalTypeSuggestion);
        }

        // Functions for the type
        typeDescriptor.getFunctions().forEach(typeFunctionDescriptor -> {
            Suggestion functionSuggestion = SuggestionFactory.create(allTypesMap, typeFunctionDescriptor);
            typeTrie.insert(functionSuggestion);
        });

        // Properties for the type
        typeDescriptor.getProperties().forEach(typePropertyDescriptor -> {
            Suggestion propertySuggestion = SuggestionFactory.create(allTypesMap, typePropertyDescriptor);
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
        Trie trie = new TrieRoot();
        descriptor.getArguments()
                .stream()
                .map(scriptSignatureArgument -> SuggestionFactory.create(allTypesMap, scriptSignatureArgument))
                .forEach(trie::insert);
        return trie;
    }
}
