package com.reedelk.plugin.service.module.impl.component.completion;

import com.reedelk.module.descriptor.model.component.ComponentOutputDescriptor;
import com.reedelk.plugin.commons.ToPresentableType;
import com.reedelk.runtime.api.commons.StringUtils;
import com.reedelk.runtime.api.message.MessageAttributes;
import com.reedelk.runtime.api.message.MessagePayload;

import java.util.*;
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

    private static Collection<Suggestion> autoComplete(Trie current, TrieMapWrapper typeAndTrieMap, String token, ComponentOutputDescriptor descriptor) {
        Collection<Suggestion> suggestions = current.autocomplete(token, typeAndTrieMap);

        // TODO: Do not recreate a suggestion all the time!
        // For each suggestion we need to pick up the extends type and figure out the display name.
        List<Suggestion> finalSuggestions = new ArrayList<>();
        for (Suggestion suggestion : suggestions) {
            if (MessagePayload.class.getName().equals(suggestion.typeText()) ||
                    MessageAttributes.class.getName().equals(suggestion.typeText())) {
                getRealType(suggestion.typeText(), descriptor).forEach(type -> {
                    // We need to create artificial suggestions due to the nature
                    // of the message payload type.
                    Suggestion dynamicSuggestion = Suggestion.create(suggestion.getType())
                            .withType(type)
                            .withName(suggestion.name())
                            .withCursorOffset(suggestion.cursorOffset())
                            .withLookupString(suggestion.lookupString())
                            .withPresentableText(suggestion.presentableText())
                            .withPresentableType(getRealVisualType(suggestion.typeText(), typeAndTrieMap, descriptor))
                            .build();
                    finalSuggestions.add(dynamicSuggestion);
                });

            } else {
                finalSuggestions.add(suggestion);
            }
        }

        return finalSuggestions;
    }

    private static String getRealVisualType(String returnType, TrieMapWrapper typeAndTrieMap, ComponentOutputDescriptor descriptor) {
        if (descriptor == null) return Object.class.getName();
        if(returnType.equals(MessagePayload.class.getName())) {
            // TODO: Descriptor might be null!
            List<String> payload = Optional.ofNullable(descriptor.getPayload()).orElse(singletonList(Object.class.getName()));
            return payload.stream().map(payloadFullyQualifiedName -> {
                Trie orDefault = typeAndTrieMap.getOrDefault(payloadFullyQualifiedName, null);
                return presentableTypeOfTrie(payloadFullyQualifiedName, orDefault);
            }).collect(Collectors.joining(","));
        }
        if (returnType.equals(MessageAttributes.class.getName())) {
            return MessageAttributes.class.getSimpleName();
        }
        return returnType;
    }

    public static String presentableTypeOfTrie(String payloadFullyQualifiedName, Trie orDefault) {
        if (orDefault != null && StringUtils.isNotBlank(orDefault.listItemType())) {
            // IT IS A LIST
            String s = orDefault.listItemType(); // TODO: Here you should use the display name. Actually for everywhere must used displayname.
            String s1 = typeNormalizer(orDefault.extendsType());
            return ToPresentableType.from(s1) + "<" + ToPresentableType.from(s) + ">";
        } else {
            return ToPresentableType.from(payloadFullyQualifiedName);
        }
    }

    private static String typeNormalizer(String type) {
        if (ArrayList.class.getName().equals(type)) {
            return "List"; // TODO: Do something here ...
        }
        return type;
    }

    private static List<String> getRealType(String returnType, ComponentOutputDescriptor descriptor) {
        String finalType = returnType;
        if (returnType.equals(MessageAttributes.class.getName())) {
            finalType = descriptor != null ? descriptor.getAttributes() : MessageAttributes.class.getName();
        } else if(returnType.equals(MessagePayload.class.getName())) {
            // TODO: Use trie wrapper TrieWrapper
            return descriptor != null ? descriptor.getPayload() : singletonList(Object.class.getName());
        }
        return Arrays.asList(finalType);
    }
}
