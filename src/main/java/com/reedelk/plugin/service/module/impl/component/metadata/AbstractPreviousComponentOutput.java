package com.reedelk.plugin.service.module.impl.component.metadata;

import com.reedelk.plugin.service.module.impl.component.completion.*;
import com.reedelk.runtime.api.message.MessageAttributes;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static com.reedelk.plugin.service.module.impl.component.completion.Suggestion.Type.PROPERTY;
import static com.reedelk.runtime.api.commons.StringUtils.isNotBlank;
import static java.util.stream.Collectors.toList;

abstract class AbstractPreviousComponentOutput implements PreviousComponentOutput {

    public static MetadataTypeDTO mergeMetadataTypes(List<MetadataTypeDTO> metadataTypes) {
        Map<String, MetadataTypeItemDTO> nameAndMetadataMappings = new HashMap<>();
        metadataTypes.forEach(metadataTypeDTO -> {
            metadataTypeDTO.getProperties().forEach(metadataTypeItemDTO -> {
                MetadataTypeItemDTO item = nameAndMetadataMappings.get(metadataTypeItemDTO.name);
                if (item != null) {
                    MetadataTypeItemDTO merged = merge(metadataTypeItemDTO, item);
                    nameAndMetadataMappings.put(metadataTypeItemDTO.name, merged);
                } else {
                    nameAndMetadataMappings.put(metadataTypeItemDTO.name, metadataTypeItemDTO);
                }
            });
        });

        return new MetadataTypeDTO(
                MessageAttributes.class.getName(),
                MessageAttributes.class.getName(),
                nameAndMetadataMappings.values());
    }

    public static MetadataTypeItemDTO merge(MetadataTypeItemDTO dto1, MetadataTypeItemDTO dto2) {
        return Objects.equals(dto1.value, dto2.value) ?
                dto1 : new MetadataTypeItemDTO(dto1.name, dto1.value + ", " + dto2.value);
    }

    protected MetadataTypeDTO createMetadataType(CompletionFinder completionFinder, TypeAndTries typeAndTries, String type) {
        Trie typeTrie = typeAndTries.getOrDefault(type);
        if (isNotBlank(typeTrie.listItemType())) {
            // Unroll list item type
            return unrollListType(completionFinder, typeAndTries, typeTrie, type);

        } else {
            List<Suggestion> typeSuggestions = suggestionsFromType(completionFinder, typeAndTries, type);
            String propertyDisplayType = TypeUtils.toSimpleName(type, typeAndTries);
            Collection<MetadataTypeItemDTO> typeProperties = new ArrayList<>();
            for (Suggestion suggestion : typeSuggestions) {
                MetadataTypeItemDTO typeProperty =
                        createTypeProperties(completionFinder, typeAndTries, suggestion.getLookupToken(), suggestion.getReturnType());
                typeProperties.add(typeProperty);
            }
            return new MetadataTypeDTO(propertyDisplayType, type, typeProperties);
        }
    }

    protected MetadataTypeItemDTO createTypeProperties(CompletionFinder completionFinder, TypeAndTries typeAndTries, String lookupToken, String type) {
        List<Suggestion> typeSuggestions = suggestionsFromType(completionFinder, typeAndTries, type);
        if (typeSuggestions.isEmpty()) {
            Trie typeTrie = typeAndTries.getOrDefault(type);
            if (isNotBlank(typeTrie.listItemType())) {
                MetadataTypeDTO listComplexType = unrollListType(completionFinder, typeAndTries, typeTrie, type);
                return new MetadataTypeItemDTO(lookupToken, listComplexType);
            } else {
                String propertyDisplayType = TypeUtils.toSimpleName(type, typeAndTries);
                return new MetadataTypeItemDTO(lookupToken, propertyDisplayType);
            }
        } else {
            MetadataTypeDTO metadataType = createMetadataType(completionFinder, typeAndTries, type);
            return new MetadataTypeItemDTO(lookupToken, metadataType);
        }
    }

    @NotNull
    protected MetadataTypeDTO unrollListType(CompletionFinder completionFinder, TypeAndTries typeAndTries, Trie typeTrie, String type) {
        // Unroll list item type
        String listItemType = typeTrie.listItemType();

        List<Suggestion> listTypeSuggestions = suggestionsFromType(completionFinder, typeAndTries, listItemType);
        Collection<MetadataTypeItemDTO> listTypeProperties = new ArrayList<>();
        for (Suggestion suggestion : listTypeSuggestions) {
            MetadataTypeItemDTO typeProperty =
                    createTypeProperties(completionFinder, typeAndTries, suggestion.getLookupToken(), suggestion.getReturnType());
            listTypeProperties.add(typeProperty);
        }

        String listDisplayType = TypeUtils.formatListDisplayType(listItemType, typeTrie);
        return new MetadataTypeDTO(listDisplayType, type, listTypeProperties);
    }

    protected List<Suggestion> suggestionsFromType(CompletionFinder completionFinder, TypeAndTries typeAndTries, String type) {
        Trie typeTrie = typeAndTries.getOrDefault(type);
        return completionFinder.find(typeTrie, this)
                .stream()
                .filter(suggestion -> PROPERTY.equals(suggestion.getType()))
                .collect(toList());
    }
}
