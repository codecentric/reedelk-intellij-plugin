package com.reedelk.plugin.service.module.impl.component.metadata;

import com.intellij.openapi.module.Module;
import com.reedelk.module.descriptor.model.component.ComponentOutputDescriptor;
import com.reedelk.plugin.service.module.PlatformModuleService;
import com.reedelk.plugin.service.module.impl.component.ComponentContext;
import com.reedelk.plugin.service.module.impl.component.completion.*;
import com.reedelk.runtime.api.message.MessageAttributes;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static com.reedelk.plugin.service.module.impl.component.completion.Suggestion.Type.PROPERTY;
import static com.reedelk.runtime.api.commons.StringUtils.isNotBlank;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

public class OutputDescriptorBuilder {

    private final CompletionFinder completionFinder;
    private final TypeAndTries typeAndTries;
    private final PlatformModuleService moduleService;
    private final Module module;

    public OutputDescriptorBuilder(Module module, PlatformModuleService moduleService, CompletionFinder completionFinder, TypeAndTries typeAndTries) {
        this.completionFinder = completionFinder;
        this.typeAndTries = typeAndTries;
        this.moduleService = moduleService;
        this.module = module;
    }

    public MetadataActualInputDTO build(ComponentContext context) {
        Optional<? extends ComponentOutputDescriptor> componentOutputDescriptor =
                DiscoveryStrategyFactory.get(module, moduleService, typeAndTries, context, context.node());

        return componentOutputDescriptor.map(descriptor -> {
            if (descriptor instanceof MultipleMessages) {
                return new MetadataActualInputDTO();
            } else {
                String description = descriptor.getDescription();
                MetadataTypeDTO outputAttributes = attributes(descriptor);
                List<MetadataTypeDTO> outputPayload = payload(descriptor);
                return new MetadataActualInputDTO(outputAttributes, outputPayload, description);
            }
        }).orElse(null);
    }

    private MetadataTypeDTO attributes(ComponentOutputDescriptor output) {
        String attributeType = output != null ? output.getAttributes() : MessageAttributes.class.getName();
        Collection<MetadataTypeItemDTO> suggestions = findAndMapDTO(output, attributeType);
        return new MetadataTypeDTO(MessageAttributes.class.getName(), suggestions);
    }

    private List<MetadataTypeDTO> payload(ComponentOutputDescriptor output) {
        List<String> outputPayloadTypes = output != null ? output.getPayload() : singletonList(Object.class.getName());
        return outputPayloadTypes
                .stream()
                .map(outputType -> map(output, outputType))
                .collect(toList());
    }

    private MetadataTypeDTO map(ComponentOutputDescriptor output, String type) {
        Trie typeTrie = typeAndTries.getOrDefault(type, Default.UNKNOWN);
        if (isNotBlank(typeTrie.listItemType())) {
            // It is  a List type, we need to find the suggestions for the List item type.
            // The list type display is: List<FileType> : FileType
            String typeDisplay = TypeUtils.formatListDisplayType(type, typeTrie);
            String listItemType = typeTrie.listItemType();
            Collection<MetadataTypeItemDTO> typeDTOs = findAndMapDTO(output, listItemType);
            return new MetadataTypeDTO(typeDisplay, typeDTOs);

        } else {
            String typeDisplay = TypeUtils.toSimpleName(type, typeTrie);
            Collection<MetadataTypeItemDTO> typeDTOs = findAndMapDTO(output, typeTrie);
            return new MetadataTypeDTO(typeDisplay, typeDTOs);
        }
    }

    private Collection<MetadataTypeItemDTO> findAndMapDTO(ComponentOutputDescriptor output, String type) {
        Trie typeTrie = typeAndTries.getOrDefault(type, Default.UNKNOWN);
        return findAndMapDTO(output, typeTrie);
    }

    private Collection<MetadataTypeItemDTO> findAndMapDTO(ComponentOutputDescriptor output, Trie typeTrie) {
        return find(typeTrie, output)
                .stream()
                .sorted(Comparator.comparing(Suggestion::getInsertValue))
                .map(mapper(output))
                .collect(toList());
    }

    private Function<Suggestion, MetadataTypeItemDTO> mapper(ComponentOutputDescriptor output) {
        return suggestion -> {
            String type = suggestion.getReturnType();
            Trie typeTrie = typeAndTries.getOrDefault(type, Default.UNKNOWN);
            if (isNotBlank(typeTrie.listItemType())) {
                // Unroll the list type
                String listItemType = typeTrie.listItemType();
                Trie listItemTrie = typeAndTries.getOrDefault(listItemType, Default.UNKNOWN);
                // The list type display is: List<FileType> : FileType
                String typeDisplay = TypeUtils.formatListDisplayType(type, typeTrie);
                return asTypeDTO(output, listItemType, listItemTrie,
                        suggestion.getInsertValue(),
                        typeDisplay);
            } else {
                return asTypeDTO(output, type, typeTrie,
                        suggestion.getInsertValue(),
                        suggestion.getReturnTypeDisplayValue());
            }
        };
    }

    private MetadataTypeItemDTO asTypeDTO(ComponentOutputDescriptor output, String type, Trie typeTrie, String propertyName, String propertyType) {
        Collection<Suggestion> suggestions = find(typeTrie, output);
        if (suggestions.isEmpty()) {
            // Simple name/value
            return new MetadataTypeItemDTO(propertyName, propertyType);
        } else {
            // Complex type
            String finalPropertyName = propertyName + " : " + propertyType;
            MetadataTypeDTO metadataTypeDTO = map(output, type);
            return new MetadataTypeItemDTO(finalPropertyName, metadataTypeDTO);
        }
    }

    private Collection<Suggestion> find(Trie trie, ComponentOutputDescriptor output) {
        return completionFinder.find(trie, output)
                .stream()
                .filter(suggestion -> PROPERTY.equals(suggestion.getType()))
                .collect(toList());
    }
}
