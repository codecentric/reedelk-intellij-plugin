package com.reedelk.plugin.service.module.impl.component.metadata;

import com.reedelk.plugin.service.module.impl.component.completion.*;
import com.reedelk.runtime.api.message.MessageAttributes;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static com.reedelk.plugin.service.module.impl.component.completion.Suggestion.Type.PROPERTY;
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

        // TODO: Replace with defaults the Message Attributes.class.
        return new MetadataTypeDTO(MessageAttributes.class, nameAndMetadataMappings.values());
    }

    public static MetadataTypeItemDTO merge(MetadataTypeItemDTO dto1, MetadataTypeItemDTO dto2) {
        return Objects.equals(dto1.value, dto2.value) ?
                dto1 :
                new MetadataTypeItemDTO(dto1.name, dto1.value + ", " + dto2.value);
    }

    protected MetadataTypeDTO createMetadataType(CompletionFinder completionFinder, TypeAndTries typeAndTries, TypeProxy typeProxy) {
        if (typeProxy.isList(typeAndTries)) {
            return unrollListType(completionFinder, typeAndTries, typeProxy);
        } else {
            List<Suggestion> typeSuggestions = suggestionsFromType(completionFinder, typeAndTries, typeProxy);
            String propertyDisplayType = typeProxy.toSimpleName(typeAndTries);
            Collection<MetadataTypeItemDTO> typeProperties = new ArrayList<>();
            for (Suggestion suggestion : typeSuggestions) {
                String returnType = suggestion.getReturnType();
                TypeProxy returnTypeProxy = TypeProxy.create(returnType);
                MetadataTypeItemDTO typeProperty =
                        createTypeProperties(completionFinder, typeAndTries, suggestion.getLookupToken(), returnTypeProxy);
                typeProperties.add(typeProperty);
            }
            return new MetadataTypeDTO(propertyDisplayType, typeProxy, typeProperties);
        }
    }

    protected MetadataTypeItemDTO createTypeProperties(CompletionFinder completionFinder, TypeAndTries typeAndTries, String lookupToken, TypeProxy typeProxy) {
        List<Suggestion> typeSuggestions = suggestionsFromType(completionFinder, typeAndTries, typeProxy);
        if (typeSuggestions.isEmpty()) {
            if (typeProxy.isList(typeAndTries)) {
                MetadataTypeDTO listComplexType = unrollListType(completionFinder, typeAndTries, typeProxy);
                return new MetadataTypeItemDTO(lookupToken, listComplexType);
            } else {
                String propertyDisplayType = typeProxy.toSimpleName(typeAndTries);
                return new MetadataTypeItemDTO(lookupToken, propertyDisplayType);
            }
        } else {
            MetadataTypeDTO metadataType = createMetadataType(completionFinder, typeAndTries, typeProxy);
            return new MetadataTypeItemDTO(lookupToken, metadataType);
        }
    }

    @NotNull
    protected MetadataTypeDTO unrollListType(CompletionFinder completionFinder, TypeAndTries typeAndTries, TypeProxy typeProxy) {
        // Unroll list item type
        String listItemType = typeProxy.listItemType(typeAndTries);

        List<Suggestion> listTypeSuggestions = suggestionsFromType(completionFinder, typeAndTries, typeProxy);
        Collection<MetadataTypeItemDTO> listTypeProperties = new ArrayList<>();

        for (Suggestion suggestion : listTypeSuggestions) {
            String returnType = suggestion.getReturnType();
            MetadataTypeItemDTO typeProperty =
                    createTypeProperties(completionFinder, typeAndTries, suggestion.getLookupToken(), TypeProxy.create(returnType));
            listTypeProperties.add(typeProperty);
        }

        // List<ItemType> : ItemType
        String listDisplayType = TypeUtils.formatUnrolledListDisplayType(typeProxy, typeAndTries);
        return new MetadataTypeDTO(listDisplayType, TypeProxy.create(listItemType), listTypeProperties);
    }

    protected List<Suggestion> suggestionsFromType(CompletionFinder completionFinder, TypeAndTries typeAndTries, TypeProxy typeProxy) {
        Trie typeTrie = typeProxy.resolve(typeAndTries);
        return completionFinder.find(typeTrie, this)
                .stream()
                .filter(suggestion -> PROPERTY.equals(suggestion.getType()))
                .collect(toList());
    }
}
