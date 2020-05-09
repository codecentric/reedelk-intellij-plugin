package com.reedelk.plugin.service.module.impl.component.completion;

import com.reedelk.module.descriptor.model.component.ComponentOutputDescriptor;
import com.reedelk.plugin.service.module.impl.component.completion.commons.PresentableType;
import com.reedelk.runtime.api.commons.StringUtils;
import com.reedelk.runtime.api.message.MessageAttributes;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

import static com.reedelk.plugin.service.module.impl.component.completion.Suggestion.Type.PROPERTY;
import static java.util.stream.Collectors.toList;

public class ComponentInputProcessor {

    private static final Trie UNKNOWN_TYPE_TRIE = new TrieDefault();
    private final TrieMapWrapper typeAndAndTries;

    public ComponentInputProcessor(TrieMapWrapper typeAndAndTries) {
        this.typeAndAndTries = typeAndAndTries;
    }

    public ComponentIO.IOTypeDescriptor outputAttributesFrom(ComponentOutputDescriptor output) {
        String attributeType = output != null ? output.getAttributes() : MessageAttributes.class.getName();

        Trie orDefault = typeAndAndTries.getOrDefault(attributeType, UNKNOWN_TYPE_TRIE);
        Collection<ComponentIO.IOTypeDTO> suggestions =
                CompletionFinder.find(orDefault, typeAndAndTries, output, new String[]{""})
                        .stream().filter(suggestion -> {
                    return suggestion.getType().equals(PROPERTY); // We only want properties in the component INput output panel.
                }).sorted(Comparator.comparing(Suggestion::lookupString))
                        .map(mapper(typeAndAndTries, output))
                        .collect(toList());

        return new ComponentIO.IOTypeDescriptor(MessageAttributes.class.getName(), suggestions);
    }

    public List<ComponentIO.IOTypeDescriptor> outputPayloadFrom(ComponentOutputDescriptor output) {
        List<String> outputPayloadTypes = output != null ?
                output.getPayload() : Collections.singletonList(Object.class.getName());
        return compute(output, outputPayloadTypes);
    }

    @NotNull
    private List<ComponentIO.IOTypeDescriptor> compute(ComponentOutputDescriptor output, List<String> outputPayloadTypes) {
        return outputPayloadTypes.stream().map(outputType -> {
            Trie typeTrie = typeAndAndTries.getOrDefault(outputType, UNKNOWN_TYPE_TRIE);
            if (StringUtils.isNotBlank(typeTrie.listItemType())) {
                Trie itemTypeTrie = typeAndAndTries.getOrDefault(typeTrie.listItemType(), UNKNOWN_TYPE_TRIE);
                Collection<ComponentIO.IOTypeDTO> suggestions = CompletionFinder.find(itemTypeTrie, typeAndAndTries, output, new String[]{""})
                        .stream().filter(suggestion -> {
                            return suggestion.getType().equals(PROPERTY); // We only want properties in the component INput output panel.
                        }).map(mapper(typeAndAndTries, output))
                        .collect(toList());
                // List<FileType> : FileType
                String typeDisplay = CompletionFinder.presentableTypeOfTrie(outputType, typeTrie) + " : " + PresentableType.from(typeTrie.listItemType());
                return new ComponentIO.IOTypeDescriptor(typeDisplay, suggestions);

            } else {
                Collection<ComponentIO.IOTypeDTO> suggestions = CompletionFinder.find(typeTrie, typeAndAndTries, output, new String[]{""})
                        .stream().filter(suggestion -> {
                            return suggestion.getType().equals(PROPERTY); // We only want properties in the component INput output panel.
                        }).map(mapper(typeAndAndTries, output))
                        .collect(toList());

                String typeDisplay = CompletionFinder.presentableTypeOfTrie(outputType, typeTrie);
                return new ComponentIO.IOTypeDescriptor(typeDisplay, suggestions);
            }

        }).collect(toList());
    }

    public Function<Suggestion, ComponentIO.IOTypeDTO> mapper(TrieMapWrapper typeAndAndTries, ComponentOutputDescriptor output) {
        return suggestion -> {
            String suggestionType = suggestion.typeText();
            Trie orDefault = typeAndAndTries.getOrDefault(suggestionType, UNKNOWN_TYPE_TRIE);
            if (StringUtils.isNotBlank(orDefault.listItemType())) {
                Trie listItemTrie = typeAndAndTries.getOrDefault(orDefault.listItemType(), UNKNOWN_TYPE_TRIE);
                Collection<Suggestion> suggestions = CompletionFinder.find(listItemTrie, typeAndAndTries, output, new String[]{""})
                        .stream().filter(suggestion1 -> suggestion1.getType().equals(PROPERTY)).collect(toList());
                if (suggestions.isEmpty()) {
                    return new ComponentIO.IOTypeDTO(suggestion.lookupString(), suggestion.presentableType());
                } else {
                    // Complex type
                    ComponentIO.IOTypeDescriptor compute = compute(output, Collections.singletonList(orDefault.listItemType())).stream().findAny().get();
                    String typeDisplay = suggestion.lookupString() + " : " + CompletionFinder.presentableTypeOfTrie(suggestionType, orDefault) + " : " + PresentableType.from(orDefault.listItemType());
                    return new ComponentIO.IOTypeDTO(typeDisplay, compute);
                }
            } else {
                Collection<Suggestion> suggestions = CompletionFinder.find(orDefault, typeAndAndTries, output, new String[]{""})
                        .stream().filter(suggestion1 -> suggestion1.getType().equals(PROPERTY)).collect(toList());
                if (suggestions.isEmpty()) {
                    return new ComponentIO.IOTypeDTO(suggestion.lookupString(), suggestion.presentableType());
                } else {
                    // Complex type
                    ComponentIO.IOTypeDescriptor compute = compute(output, Collections.singletonList(suggestionType)).stream().findAny().get();
                    return new ComponentIO.IOTypeDTO(suggestion.lookupString(), compute);
                }
            }
        };
    }

}
