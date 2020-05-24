package com.reedelk.plugin.service.module.impl.component.metadata;

import com.reedelk.plugin.service.module.impl.component.completion.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.reedelk.plugin.service.module.impl.component.completion.Suggestion.Type.PROPERTY;
import static java.util.stream.Collectors.toList;

abstract class AbstractPreviousComponentOutput implements PreviousComponentOutput {

    protected MetadataTypeDTO createMetadataType(SuggestionFinder suggester, TypeAndTries typeAndTries, TypeProxy typeProxy) {
        if (typeProxy.resolve(typeAndTries).isList()) {
            return unrollListType(suggester, typeAndTries, typeProxy);
        } else {
            List<Suggestion> typeSuggestions = suggestionPropertiesOf(suggester, typeAndTries, typeProxy);
            String propertyDisplayType = typeProxy.toSimpleName(typeAndTries);
            Collection<MetadataTypeItemDTO> typeProperties = new ArrayList<>();
            for (Suggestion suggestion : typeSuggestions) {
                TypeProxy returnTypeProxy = suggestion.getReturnType();
                MetadataTypeItemDTO typeProperty =
                        createTypeProperties(suggester, typeAndTries, suggestion.getLookupToken(), returnTypeProxy);
                typeProperties.add(typeProperty);
            }
            return new MetadataTypeDTO(propertyDisplayType, typeProxy, typeProperties);
        }
    }

    protected MetadataTypeItemDTO createTypeProperties(SuggestionFinder suggester, TypeAndTries typeAndTries, String lookupToken, TypeProxy typeProxy) {
        List<Suggestion> typeSuggestions = suggestionPropertiesOf(suggester, typeAndTries, typeProxy);
        if (typeSuggestions.isEmpty()) {
            if (typeProxy.resolve(typeAndTries).isList()) {
                MetadataTypeDTO listComplexType = unrollListType(suggester, typeAndTries, typeProxy);
                return new MetadataTypeItemDTO(lookupToken, listComplexType);
            } else {
                String propertyDisplayType = typeProxy.toSimpleName(typeAndTries);
                return new MetadataTypeItemDTO(lookupToken, propertyDisplayType);
            }
        } else {
            MetadataTypeDTO metadataType = createMetadataType(suggester, typeAndTries, typeProxy);
            return new MetadataTypeItemDTO(lookupToken, metadataType);
        }
    }

    @NotNull
    protected MetadataTypeDTO unrollListType(SuggestionFinder suggester, TypeAndTries typeAndTries, TypeProxy listTypeProxy) {
        // Unroll list item type
        TypeProxy listItemType = listTypeProxy.resolve(typeAndTries).listItemType(typeAndTries);

        List<Suggestion> listItemTypeProperties = suggestionPropertiesOf(suggester, typeAndTries, listItemType);

        String listTypeProxySimpleName = listTypeProxy.toSimpleName(typeAndTries);
        if (listItemTypeProperties.isEmpty()) {
            // We don't unroll the list item type, because there are no properties to display.
            // This is the case of lists with primitive types or unknown non primitive types.
            return new MetadataTypeDTO(listTypeProxySimpleName, listTypeProxy, Collections.emptyList());

        } else {
            Collection<MetadataTypeItemDTO> listTypeProperties = new ArrayList<>();
            for (Suggestion suggestion : listItemTypeProperties) {
                TypeProxy returnType = suggestion.getReturnType();
                MetadataTypeItemDTO typeProperty =
                        createTypeProperties(suggester, typeAndTries, suggestion.getLookupToken(), returnType);
                listTypeProperties.add(typeProperty);
            }
            // List<ItemType> : ItemType
            String listItemTypeProxySimpleName = listItemType.resolve(typeAndTries).toSimpleName(typeAndTries);
            String listDisplayType = listTypeProxySimpleName + " : " + listItemTypeProxySimpleName;
            return new MetadataTypeDTO(listDisplayType, listTypeProxy, listTypeProperties);
        }
    }

    protected List<Suggestion> suggestionPropertiesOf(SuggestionFinder suggester, TypeAndTries typeAndTries, TypeProxy typeProxy) {
        Trie typeTrie = typeProxy.resolve(typeAndTries);
        return suggester.suggest(typeTrie, this)
                .stream()
                .filter(suggestion -> PROPERTY.equals(suggestion.getType()))
                .collect(toList());
    }
}
