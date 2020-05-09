package com.reedelk.plugin.service.module.impl.component.completion.commons;

import com.reedelk.module.descriptor.model.component.ComponentOutputDescriptor;
import com.reedelk.plugin.service.module.impl.component.completion.Suggestion;
import com.reedelk.plugin.service.module.impl.component.completion.Trie;
import com.reedelk.plugin.service.module.impl.component.completion.TrieMapWrapper;
import com.reedelk.runtime.api.message.MessageAttributes;
import com.reedelk.runtime.api.message.MessagePayload;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;

public class DynamicType {

    private DynamicType() {
    }

    // Resolves the dynamic type from the output descriptor
    public static List<String> from(Suggestion suggestion, ComponentOutputDescriptor descriptor) {
        String suggestionType = suggestion.typeText();
        if (MessageAttributes.class.getName().equals(suggestionType)) {
            return descriptor != null && descriptor.getAttributes() != null ?
                    singletonList(descriptor.getAttributes()) :
                    singletonList(MessageAttributes.class.getName());

        } else if (MessagePayload.class.getName().equals(suggestionType)) {
            return descriptor != null && descriptor.getPayload() != null ?
                descriptor.getPayload() :
                    singletonList(Object.class.getName());

        } else {
            return singletonList(Object.class.getName());
        }
    }

    // We need to create an artificial suggestion for each dynamic type found.
    // The dynamic types depend on the previous component output descriptor.
    // Note that there might be multiple dynamic types because a component
    // could have multiple outputs.
    public static Collection<Suggestion> createDynamicSuggestion(TrieMapWrapper typeAndTrieMap, ComponentOutputDescriptor descriptor, Suggestion suggestion) {
        return DynamicType.from(suggestion, descriptor).stream()
                .map(dynamicType -> Suggestion.create(suggestion.getType())
                        .withType(dynamicType)
                        .withName(suggestion.name())
                        .withCursorOffset(suggestion.cursorOffset())
                        .withLookupString(suggestion.lookupString())
                        .withPresentableText(suggestion.presentableText())
                        .withPresentableType(presentableTypeOf(suggestion, dynamicType, typeAndTrieMap))
                        .build()).collect(Collectors.toList());
    }

    private static String presentableTypeOf(Suggestion suggestion, String dynamicType, TrieMapWrapper typeAndTrieMap) {
        String originalType = suggestion.typeText();
        if (MessageAttributes.class.getName().equals(originalType)) {
            return MessageAttributes.class.getSimpleName(); // We keep the message attributes.
        } else {
            Trie dynamicTypeTrie = typeAndTrieMap.getOrDefault(dynamicType, UnknownTypeTrie.get());
            return PresentableType.from(dynamicType, dynamicTypeTrie);
        }
    }

    public static boolean is(Suggestion suggestion) {
        return MessagePayload.class.getName().equals(suggestion.typeText()) ||
                MessageAttributes.class.getName().equals(suggestion.typeText());
    }
}
