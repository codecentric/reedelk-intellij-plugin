package com.reedelk.plugin.service.module.impl.component.completion;

import com.reedelk.module.descriptor.model.component.ComponentOutputDescriptor;
import com.reedelk.plugin.service.module.impl.component.completion.commons.DynamicType;
import com.reedelk.plugin.service.module.impl.component.completion.commons.PresentableType;
import com.reedelk.runtime.api.message.MessageAttributes;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

import static com.reedelk.plugin.service.module.impl.component.completion.Suggestion.Type.PROPERTY;
import static com.reedelk.runtime.api.commons.StringUtils.EMPTY;
import static com.reedelk.runtime.api.commons.StringUtils.isNotBlank;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

public class IOProcessor {

    private static final Trie UNKNOWN_TYPE_TRIE = new TrieDefault();
    private final TrieMapWrapper typeAndAndTries;
    private final CompletionFinder completionFinder;

    public IOProcessor(TrieMapWrapper typeAndAndTries, CompletionFinder completionFinder) {
        this.typeAndAndTries = typeAndAndTries;
        this.completionFinder = completionFinder;
    }

    public IOTypeDescriptor attributes(ComponentOutputDescriptor output) {
        String attributeType = output != null ? output.getAttributes() : MessageAttributes.class.getName();
        Collection<IOTypeDTO> suggestions = findAndMapDTO(output, attributeType);
        return new IOTypeDescriptor(MessageAttributes.class.getName(), suggestions);
    }

    public List<IOTypeDescriptor> payload(ComponentOutputDescriptor output) {
        List<String> outputPayloadTypes = output != null ? output.getPayload() : singletonList(Object.class.getName());
        return compute(output, outputPayloadTypes);
    }

    @NotNull
    private List<IOTypeDescriptor> compute(ComponentOutputDescriptor output, List<String> outputPayloadTypes) {
        return outputPayloadTypes.stream().map(outputType -> {
            Trie typeTrie = typeAndAndTries.getOrDefault(outputType, UNKNOWN_TYPE_TRIE);
            if (isNotBlank(typeTrie.listItemType())) {
                // It is  a list, we need to find the suggestions for the list item type.
                // List<FileType> : FileType
                String typeDisplay = DynamicType.presentableTypeOfTrie(outputType, typeTrie) + " : " + PresentableType.from(typeTrie.listItemType());
                String listItemType = typeTrie.listItemType();
                Collection<IOTypeDTO> typeDTOs = findAndMapDTO(output, listItemType);
                return new IOTypeDescriptor(typeDisplay, typeDTOs);

            } else {
                String typeDisplay = DynamicType.presentableTypeOfTrie(outputType, typeTrie);
                Collection<IOTypeDTO> typeDTOs = findAndMapDTO(output, typeTrie);
                return new IOTypeDescriptor(typeDisplay, typeDTOs);
            }
        }).collect(toList());
    }

    @NotNull
    private Collection<Suggestion> find(Trie trie, ComponentOutputDescriptor output) {
        return completionFinder.find(trie, new String[]{EMPTY}, output)
                .stream()
                .filter(suggestion -> PROPERTY.equals(suggestion.getType()))
                .collect(toList());
    }

    @NotNull
    private Collection<IOTypeDTO> findAndMapDTO(ComponentOutputDescriptor output, String type) {
        Trie typeTrie = typeAndAndTries.getOrDefault(type, UNKNOWN_TYPE_TRIE);
        return findAndMapDTO(output, typeTrie);
    }

    @NotNull
    private Collection<IOTypeDTO> findAndMapDTO(ComponentOutputDescriptor output, Trie typeTrie) {
        return find(typeTrie, output)
                .stream()
                .sorted(Comparator.comparing(Suggestion::lookupString))
                .map(mapper(output))
                .collect(toList());
    }

    public Function<Suggestion, IOTypeDTO> mapper(ComponentOutputDescriptor output) {
        return suggestion -> {
            String suggestionType = suggestion.typeText();
            Trie orDefault = typeAndAndTries.getOrDefault(suggestionType, UNKNOWN_TYPE_TRIE);
            if (isNotBlank(orDefault.listItemType())) {
                return mapListType(output, suggestion, suggestionType, orDefault);
            } else {
                Collection<Suggestion> suggestions = find(orDefault, output);
                if (suggestions.isEmpty()) {
                    return new IOTypeDTO(suggestion.lookupString(), suggestion.presentableType());
                } else {
                    // Complex type
                    IOTypeDescriptor compute = compute(output, singletonList(suggestionType)).stream().findAny().get();
                    return new IOTypeDTO(suggestion.lookupString(), compute);
                }
            }
        };
    }

    @NotNull
    private IOTypeDTO mapListType(ComponentOutputDescriptor output, Suggestion suggestion, String suggestionType, Trie orDefault) {
        Trie listItemTrie = typeAndAndTries.getOrDefault(orDefault.listItemType(), UNKNOWN_TYPE_TRIE);
        Collection<Suggestion> suggestions = find(listItemTrie, output);
        if (suggestions.isEmpty()) {
            return new IOTypeDTO(suggestion.lookupString(), suggestion.presentableType());
        } else {
            // Complex type
            IOTypeDescriptor compute = compute(output, singletonList(orDefault.listItemType())).stream().findAny().get();
            String typeDisplay = suggestion.lookupString() + " : " + DynamicType.presentableTypeOfTrie(suggestionType, orDefault) + " : " + PresentableType.from(orDefault.listItemType());
            return new IOTypeDTO(typeDisplay, compute);
        }
    }
}
