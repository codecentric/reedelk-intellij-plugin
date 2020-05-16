package com.reedelk.plugin.service.module.impl.component.metadata;

import com.intellij.openapi.module.Module;
import com.reedelk.module.descriptor.model.component.ComponentOutputDescriptor;
import com.reedelk.plugin.service.module.PlatformModuleService;
import com.reedelk.plugin.service.module.impl.component.ComponentContext;
import com.reedelk.plugin.service.module.impl.component.completion.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static com.reedelk.plugin.service.module.impl.component.completion.Suggestion.Type.PROPERTY;
import static com.reedelk.runtime.api.commons.StringUtils.isNotBlank;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

// TODO :Also add for each completion for each { it. }
public class MetadataActualInputDTOBuilder {

    private final PlatformModuleService moduleService;
    private final CompletionFinder completionFinder;
    private final TypeAndTries typeAndTries;
    private final Module module;

    public MetadataActualInputDTOBuilder(@NotNull Module module,
                                         @NotNull PlatformModuleService moduleService,
                                         @NotNull CompletionFinder completionFinder,
                                         @NotNull TypeAndTries typeAndTries) {
        this.completionFinder = completionFinder;
        this.moduleService = moduleService;
        this.typeAndTries = typeAndTries;
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

    private MetadataTypeDTO attributes(ComponentOutputDescriptor descriptor) {
        List<MetadataTypeDTO> metadataTypes = descriptor.getAttributes().stream()
                .map(attributeType -> createMetadataType(attributeType, descriptor))
                .collect(toList());
        return metadataTypes.get(0);
    }


    private List<MetadataTypeDTO> payload(ComponentOutputDescriptor output) {
        List<String> outputPayloadTypes = output != null ?
                output.getPayload() : singletonList(Object.class.getName());
        return outputPayloadTypes.stream()
                .distinct()
                .map(payloadType -> {
                    return createMetadataType(payloadType, output);
                })
                .collect(toList());
    }

    private MetadataTypeDTO createMetadataType(String type, ComponentOutputDescriptor output) {
        Trie typeTrie = typeAndTries.getOrDefault(type, Default.UNKNOWN);
        if (isNotBlank(typeTrie.listItemType())) {
            // Unroll list item type
            return unrollListType(output, typeTrie);

        } else {
            List<Suggestion> typeSuggestions = suggestionsFromType(type, output);
            String propertyDisplayType = TypeUtils.toSimpleName(type, typeAndTries);
            Collection<MetadataTypeItemDTO> typeProperties = new ArrayList<>();
            for (Suggestion suggestion : typeSuggestions) {
                MetadataTypeItemDTO typeProperty =
                        createTypeProperties(suggestion.getLookupToken(), suggestion.getReturnType(), output);
                typeProperties.add(typeProperty);
            }
            return new MetadataTypeDTO(propertyDisplayType, typeProperties);
        }
    }

    private MetadataTypeItemDTO createTypeProperties(String lookupToken, String type, ComponentOutputDescriptor output) {
        List<Suggestion> typeSuggestions = suggestionsFromType(type, output);
        if (typeSuggestions.isEmpty()) {

            Trie typeTrie = typeAndTries.getOrDefault(type, Default.UNKNOWN);
            if (isNotBlank(typeTrie.listItemType())) {
                MetadataTypeDTO listComplexType = unrollListType(output, typeTrie);
                return new MetadataTypeItemDTO(lookupToken, listComplexType);

            } else {
                String propertyDisplayType = TypeUtils.toSimpleName(type, typeAndTries);
                return new MetadataTypeItemDTO(lookupToken, propertyDisplayType);
            }

        } else {
            MetadataTypeDTO metadataType = createMetadataType(type, output);
            return new MetadataTypeItemDTO(lookupToken, metadataType);
        }
    }

    @NotNull
    private MetadataTypeDTO unrollListType(ComponentOutputDescriptor output, Trie typeTrie) {
        // Unroll list item type
        String listItemType = typeTrie.listItemType();

        List<Suggestion> listTypeSuggestions = suggestionsFromType(listItemType, output);
        Collection<MetadataTypeItemDTO> listTypeProperties = new ArrayList<>();
        for (Suggestion suggestion : listTypeSuggestions) {
            MetadataTypeItemDTO typeProperty =
                    createTypeProperties(suggestion.getLookupToken(), suggestion.getReturnType(), output);
            listTypeProperties.add(typeProperty);
        }

        String listDisplayType = TypeUtils.formatListDisplayType(listItemType, typeTrie);
        return new MetadataTypeDTO(listDisplayType, listTypeProperties);
    }

    private List<Suggestion> suggestionsFromType(String type, ComponentOutputDescriptor output) {
        Trie typeTrie = typeAndTries.getOrDefault(type, Default.UNKNOWN);
        return completionFinder.find(typeTrie, output)
                .stream()
                .filter(suggestion -> PROPERTY.equals(suggestion.getType()))
                .collect(toList());
    }
}
