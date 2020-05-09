package com.reedelk.plugin.service.module.impl.component.completion.commons;

import com.reedelk.module.descriptor.model.component.ComponentOutputDescriptor;
import com.reedelk.plugin.service.module.impl.component.completion.Suggestion;
import com.reedelk.plugin.service.module.impl.component.completion.Trie;
import com.reedelk.plugin.service.module.impl.component.completion.TrieMapWrapper;
import com.reedelk.runtime.api.message.MessageAttributes;
import com.reedelk.runtime.api.message.MessagePayload;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.reedelk.runtime.api.commons.StringUtils.isNotBlank;
import static java.util.Collections.singletonList;

public class DynamicType {

    private DynamicType() {
    }

    // Resolves the dynamic type from the output descriptor
    @SuppressWarnings("UnnecessaryLocalVariable")
    public static List<String> from(Suggestion suggestion, ComponentOutputDescriptor descriptor) {
        String suggestionType = suggestion.typeText();
        if (MessageAttributes.class.getName().equals(suggestionType)) {
            String attributeDynamicType = descriptor != null ?
                    descriptor.getAttributes() : MessageAttributes.class.getName();
            return Collections.singletonList(attributeDynamicType);

        } else if (MessagePayload.class.getName().equals(suggestionType)) {
            List<String> payloadDynamicTypes = descriptor != null ?
                    descriptor.getPayload() : singletonList(Object.class.getName());
            return payloadDynamicTypes;

        } else {
            return Collections.singletonList(Object.class.getName());
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
            return presentableTypeOfTrie(dynamicType, dynamicTypeTrie);
        }
    }

    public static String presentableTypeOfTrie(String dynamicType, Trie trie) {
        if (isNotBlank(trie.listItemType())) {
            // If exists a list item type, it is a list and we want to display it with: List<ItemType>
            String listItemType = trie.listItemType();
            return "List<" + PresentableType.from(listItemType) + ">";
        } else {
            return PresentableType.from(dynamicType);
        }
    }

    public static boolean is(Suggestion suggestion) {
        return MessagePayload.class.getName().equals(suggestion.typeText()) ||
                MessageAttributes.class.getName().equals(suggestion.typeText());
    }
}
