package com.reedelk.plugin.service.module.impl.component.completion;

import com.reedelk.module.descriptor.model.component.ComponentOutputDescriptor;
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
        return outputPayloadTypes
                .stream()
                .map(outputType -> asIOTypeDescriptor(output, outputType))
                .collect(toList());
    }

    @NotNull
    private IOTypeDescriptor asIOTypeDescriptor(ComponentOutputDescriptor output, String type) {
        Trie typeTrie = typeAndAndTries.getOrDefault(type, UNKNOWN_TYPE_TRIE);
        if (isNotBlank(typeTrie.listItemType())) {
            // It is  a List type, we need to find the suggestions for the List item type.
            // The list type display is: List<FileType> : FileType
            String typeDisplay = PresentableTypeUtils.formatListDisplayType(type, typeTrie);
            String listItemType = typeTrie.listItemType();
            Collection<IOTypeDTO> typeDTOs = findAndMapDTO(output, listItemType);
            return new IOTypeDescriptor(typeDisplay, typeDTOs);

        } else {
            String typeDisplay = PresentableTypeUtils.from(type, typeTrie);
            Collection<IOTypeDTO> typeDTOs = findAndMapDTO(output, typeTrie);
            return new IOTypeDescriptor(typeDisplay, typeDTOs);
        }
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
            Trie typeTrie = typeAndAndTries.getOrDefault(suggestionType, UNKNOWN_TYPE_TRIE);
            if (isNotBlank(typeTrie.listItemType())) {
                String listItemType = typeTrie.listItemType();
                Trie listItemTrie = typeAndAndTries.getOrDefault(listItemType, UNKNOWN_TYPE_TRIE);
                // The list type display is: List<FileType> : FileType
                String typeDisplay = PresentableTypeUtils.formatListDisplayType(suggestionType, typeTrie);
                return asTypeDTO(output, listItemType, listItemTrie,
                        suggestion.lookupString(),
                        typeDisplay);
            } else {
                return asTypeDTO(output, suggestionType, typeTrie,
                        suggestion.lookupString(),
                        suggestion.presentableType());
            }
        };
    }

    @NotNull
    private IOTypeDTO asTypeDTO(ComponentOutputDescriptor output, String suggestionType, Trie orDefault, String propertyName, String propertyType) {
        Collection<Suggestion> suggestions = find(orDefault, output);
        if (suggestions.isEmpty()) {
            return new IOTypeDTO(propertyName, propertyType);
        } else {
            // Complex type
            String finalPropertyName = propertyName + " : " + propertyType;
            IOTypeDescriptor ioTypeDescriptor = asIOTypeDescriptor(output, suggestionType);
            return new IOTypeDTO(finalPropertyName, ioTypeDescriptor);
        }
    }
}
