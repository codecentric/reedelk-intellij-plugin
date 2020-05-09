package com.reedelk.plugin.service.module.impl.component.completion;

import com.reedelk.module.descriptor.model.component.ComponentOutputDescriptor;
import com.reedelk.plugin.service.module.impl.component.completion.commons.DynamicType;
import com.reedelk.plugin.service.module.impl.component.completion.commons.PresentableType;
import com.reedelk.runtime.api.commons.StringUtils;
import com.reedelk.runtime.api.message.MessageAttributes;
import com.reedelk.runtime.api.message.MessagePayload;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;

public class CompletionFinder {

    private static final Trie UNKNOWN_TYPE_TRIE = new TrieDefault();

    public static Collection<Suggestion> find(Trie root, TrieMapWrapper typeAndTrieMap, ComponentOutputDescriptor componentOutputDescriptor, String[] tokens) {
        Trie current = root;
        Collection<Suggestion> autocompleteResults = new ArrayList<>();
        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i];
            if (current == null) {
                autocompleteResults = new ArrayList<>();
            } else if (i == tokens.length - 1) {
                autocompleteResults = autoComplete(current, typeAndTrieMap, token, componentOutputDescriptor);
            } else {
                // One match -> Multiple types...
                // We need to find the exact match, all the exact match must be wrapped in a trie wrapper
                List<Trie> collect = autoComplete(current, typeAndTrieMap, token, componentOutputDescriptor).stream()
                        .filter(suggestion -> {
                            return suggestion.name().equals(token); // We only keep exact matches. If there are, we can move forward, otherwise we stop.
                        }).map(suggestion -> typeAndTrieMap.getOrDefault(suggestion.typeText(), UNKNOWN_TYPE_TRIE)).collect(Collectors.toList());

                if (collect.isEmpty()) {
                    // Could not found a match to follow for the current token.
                    // Therefore no match is found.
                    break;
                }

                current = new TrieWrapper(collect);
            }
        }
        return autocompleteResults;
    }

    private static boolean isDynamicSuggestion(Suggestion suggestion) {
        return MessagePayload.class.getName().equals(suggestion.typeText()) ||
                MessageAttributes.class.getName().equals(suggestion.typeText());
    }

    private static Collection<Suggestion> autoComplete(Trie current, TrieMapWrapper typeAndTrieMap, String token, ComponentOutputDescriptor descriptor) {
        Collection<Suggestion> suggestions = current.autocomplete(token, typeAndTrieMap);
        List<Suggestion> finalSuggestions = new ArrayList<>();
        for (Suggestion suggestion : suggestions) {
            if (isDynamicSuggestion(suggestion)) {
                Collection<Suggestion> dynamicSuggestion =
                        createDynamicSuggestion(typeAndTrieMap, descriptor, suggestion);
                finalSuggestions.addAll(dynamicSuggestion);
            } else {
                finalSuggestions.add(suggestion);
            }
        }
        return finalSuggestions;
    }

    // We need to create an artificial suggestion for each dynamic type found.
    // The dynamic types depend on the previous component output descriptor.
    // Note that there might be multiple dynamic types because a component
    // could have multiple outputs.
    private static Collection<Suggestion> createDynamicSuggestion(TrieMapWrapper typeAndTrieMap, ComponentOutputDescriptor descriptor, Suggestion suggestion) {
        return DynamicType.from(suggestion, descriptor).stream()
                .map(dynamicType -> Suggestion.create(suggestion.getType())
                        .withType(dynamicType)
                        .withName(suggestion.name())
                        .withCursorOffset(suggestion.cursorOffset())
                        .withLookupString(suggestion.lookupString())
                        .withPresentableText(suggestion.presentableText())
                        .withPresentableType(presentableTypeOf(suggestion.typeText(), typeAndTrieMap, descriptor))
                        .build()).collect(Collectors.toList());
    }

    private static String presentableTypeOf(String returnType, TrieMapWrapper typeAndTrieMap, ComponentOutputDescriptor descriptor) {
        if (descriptor == null) return PresentableType.from(Object.class.getName());
        if (MessagePayload.class.getName().equals(returnType)) {
            // The descriptor
            List<String> payload = Optional.ofNullable(descriptor.getPayload()).orElse(singletonList(Object.class.getName()));
            return payload.stream().map(payloadFullyQualifiedName -> {
                Trie orDefault = typeAndTrieMap.getOrDefault(payloadFullyQualifiedName, null);
                return presentableTypeOfTrie(payloadFullyQualifiedName, orDefault);
            }).collect(Collectors.joining(","));
        }
        if (MessageAttributes.class.getName().equals(returnType)) {
            return MessageAttributes.class.getSimpleName();
        }
        return returnType;
    }

    public static String presentableTypeOfTrie(String payloadFullyQualifiedName, Trie orDefault) {
        if (orDefault != null && StringUtils.isNotBlank(orDefault.listItemType())) {
            // IT IS A LIST
            String s = orDefault.listItemType(); // TODO: Here you should use the display name. Actually for everywhere must used displayname.
            String s1 = typeNormalizer(orDefault.extendsType());
            return PresentableType.from(s1) + "<" + PresentableType.from(s) + ">";
        } else {
            return PresentableType.from(payloadFullyQualifiedName);
        }
    }

    private static String typeNormalizer(String type) {
        if (ArrayList.class.getName().equals(type)) {
            return "List"; // TODO: Do something here ...
        }
        return type;
    }
}
