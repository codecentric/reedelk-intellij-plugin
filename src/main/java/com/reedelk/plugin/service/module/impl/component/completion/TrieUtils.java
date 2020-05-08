package com.reedelk.plugin.service.module.impl.component.completion;

import com.reedelk.module.descriptor.model.type.TypeDescriptor;

import java.util.Map;

import static com.reedelk.plugin.service.module.impl.component.completion.Suggestion.Type.FUNCTION;
import static com.reedelk.plugin.service.module.impl.component.completion.Suggestion.Type.PROPERTY;

public class TrieUtils {

    public static void populate(Trie global, Map<String, Trie> typeAndTriesMap, TypeDescriptor typeDescriptor) {
        // Global root type
        if (typeDescriptor.isGlobal()) {
            Suggestion globalTypeProperty = Suggestion.create(PROPERTY)
                    .withLookupString(typeDescriptor.getDisplayName())
                    .withType(typeDescriptor.getType())
                    .build();
            global.insert(globalTypeProperty);
        }

        final Trie typeTrie = new TrieDefault(typeDescriptor.getExtendsType(), typeDescriptor.getListItemType());

        // Functions for the type
        typeDescriptor.getFunctions().forEach(typeFunctionDescriptor -> {
            Suggestion functionSuggestion = Suggestion.create(FUNCTION)
                    .withName(typeFunctionDescriptor.getName())
                    .withLookupString(typeFunctionDescriptor.getName() + "()")
                    .withPresentableText(typeFunctionDescriptor.getSignature())
                    .withCursorOffset(typeFunctionDescriptor.getCursorOffset())
                    .withType(typeFunctionDescriptor.getReturnType())
                    .build();
            typeTrie.insert(functionSuggestion);
        });

        // Properties for the type
        typeDescriptor.getProperties().forEach(typePropertyDescriptor -> {
            Suggestion propertySuggestion = Suggestion.create(PROPERTY)
                    .withLookupString(typePropertyDescriptor.getName())
                    .withType(typePropertyDescriptor.getType())
                    .build();
            typeTrie.insert(propertySuggestion);
        });

        typeAndTriesMap.put(typeDescriptor.getType(), typeTrie);
    }
}
