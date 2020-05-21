package com.reedelk.plugin.service.module.impl.component.metadata;

import com.reedelk.plugin.exception.PluginException;
import com.reedelk.plugin.service.module.impl.component.completion.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static com.reedelk.plugin.service.module.impl.component.completion.Suggestion.Type.PROPERTY;
import static java.util.stream.Collectors.toList;

abstract class AbstractPreviousComponentOutput implements PreviousComponentOutput {

    public static MetadataTypeDTO mergeMetadataTypes(List<MetadataTypeDTO> metadataTypes, TypeAndTries typeAndTries) {
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

        TypeProxy typeProxy = TypeProxy.create(TypeDefault.DEFAULT_ATTRIBUTES);
        String displayName = typeProxy.toSimpleName(typeAndTries);
        return new MetadataTypeDTO(displayName, typeProxy, nameAndMetadataMappings.values());
    }

    public static MetadataTypeItemDTO merge(MetadataTypeItemDTO dto1, MetadataTypeItemDTO dto2) {
        return Objects.equals(dto1.value, dto2.value) ?
                dto1 :
                new MetadataTypeItemDTO(dto1.name, dto1.value + ", " + dto2.value);
    }

    protected MetadataTypeDTO createMetadataType(SuggestionFinder suggestionFinder, TypeAndTries typeAndTries, TypeProxy typeProxy) {
        if (typeProxy.resolve(typeAndTries).isList()) {
            return unrollListType(suggestionFinder, typeAndTries, typeProxy);
        } else {
            List<Suggestion> typeSuggestions = suggestionsFromType(suggestionFinder, typeAndTries, typeProxy);
            String propertyDisplayType = typeProxy.toSimpleName(typeAndTries);
            Collection<MetadataTypeItemDTO> typeProperties = new ArrayList<>();
            for (Suggestion suggestion : typeSuggestions) {
                TypeProxy returnTypeProxy = suggestion.getReturnType();
                MetadataTypeItemDTO typeProperty =
                        createTypeProperties(suggestionFinder, typeAndTries, suggestion.getLookupToken(), returnTypeProxy);
                typeProperties.add(typeProperty);
            }
            return new MetadataTypeDTO(propertyDisplayType, typeProxy, typeProperties);
        }
    }

    protected MetadataTypeItemDTO createTypeProperties(SuggestionFinder suggestionFinder, TypeAndTries typeAndTries, String lookupToken, TypeProxy typeProxy) {
        List<Suggestion> typeSuggestions = suggestionsFromType(suggestionFinder, typeAndTries, typeProxy);
        if (typeSuggestions.isEmpty()) {
            if (typeProxy.resolve(typeAndTries).isList()) {
                MetadataTypeDTO listComplexType = unrollListType(suggestionFinder, typeAndTries, typeProxy);
                return new MetadataTypeItemDTO(lookupToken, listComplexType);
            } else {
                String propertyDisplayType = typeProxy.toSimpleName(typeAndTries);
                return new MetadataTypeItemDTO(lookupToken, propertyDisplayType);
            }
        } else {
            MetadataTypeDTO metadataType = createMetadataType(suggestionFinder, typeAndTries, typeProxy);
            return new MetadataTypeItemDTO(lookupToken, metadataType);
        }
    }

    @NotNull
    protected MetadataTypeDTO unrollListType(SuggestionFinder suggestionFinder, TypeAndTries typeAndTries, TypeProxy listTypeProxy) {
        // Unroll list item type
        TypeProxy listItemType = listTypeProxy.resolve(typeAndTries).listItemType(typeAndTries);

        List<Suggestion> listTypeSuggestions = suggestionsFromType(suggestionFinder, typeAndTries, listItemType);
        Collection<MetadataTypeItemDTO> listTypeProperties = new ArrayList<>();

        for (Suggestion suggestion : listTypeSuggestions) {
            TypeProxy returnType = suggestion.getReturnType();
            MetadataTypeItemDTO typeProperty =
                    createTypeProperties(suggestionFinder, typeAndTries, suggestion.getLookupToken(), returnType);
            listTypeProperties.add(typeProperty);
        }

        // List<ItemType> : ItemType
        String listTypeProxySimpleName = listTypeProxy.toSimpleName(typeAndTries);
        String listItemTypeProxySimpleName = listItemType.resolve(typeAndTries).toSimpleName(typeAndTries);
        String listDisplayType = listTypeProxySimpleName + " : " + listItemTypeProxySimpleName;
        return new MetadataTypeDTO(listDisplayType, listTypeProxy, listTypeProperties);
    }

    protected List<Suggestion> suggestionsFromType(SuggestionFinder suggestionFinder, TypeAndTries typeAndTries, TypeProxy typeProxy) {
        Trie typeTrie = typeProxy.resolve(typeAndTries);
        return suggestionFinder.suggest(typeTrie, this)
                .stream()
                .filter(suggestion -> PROPERTY.equals(suggestion.getType()))
                .collect(toList());
    }

    /**
     * Takes a group of suggestions which refer to the same lookup string and collapses them into one
     * single suggestion with type equal to a comma separated list of all the suggestions in the group.
     */
    protected Suggestion flatten(Collection<Suggestion> suggestions, TypeAndTries typeAndTrieMap) {
        if (suggestions.size() == 1) return suggestions.iterator().next();

        Suggestion suggestion = suggestions.stream()
                .findAny()
                .orElseThrow(() -> new PluginException("Expected at least one dynamic suggestion."));
        List<String> possibleTypes = suggestions.stream()
                .map(theSuggestion -> theSuggestion.getReturnType().toSimpleName(typeAndTrieMap))
                .collect(toList());
        return Suggestion.create(suggestion.getType())
                .insertValue(suggestion.getInsertValue())
                .tailText(suggestion.getTailText())
                .lookupToken(suggestion.getLookupToken())
                // The return type for 'flattened' suggestions is never used because this suggestion is only created for a terminal token.
                .returnType(TypeProxy.FLATTENED)
                .returnTypeDisplayValue(String.join(",", possibleTypes))
                .build();
    }
}
