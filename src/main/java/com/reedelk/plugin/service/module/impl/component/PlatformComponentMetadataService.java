package com.reedelk.plugin.service.module.impl.component;

import com.intellij.openapi.module.Module;
import com.reedelk.module.descriptor.model.component.ComponentInputDescriptor;
import com.reedelk.module.descriptor.model.component.ComponentOutputDescriptor;
import com.reedelk.plugin.commons.Topics;
import com.reedelk.plugin.exception.PluginException;
import com.reedelk.plugin.executor.PluginExecutors;
import com.reedelk.plugin.service.module.PlatformModuleService;
import com.reedelk.plugin.service.module.impl.component.completion.*;
import com.reedelk.plugin.service.module.impl.component.metadata.*;
import com.reedelk.runtime.api.message.MessageAttributes;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static com.reedelk.plugin.message.ReedelkBundle.message;
import static com.reedelk.plugin.service.module.impl.component.completion.Suggestion.Type.PROPERTY;
import static com.reedelk.runtime.api.commons.StringUtils.isNotBlank;
import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

class PlatformComponentMetadataService implements PlatformModuleService {

    private final Module module;
    private final TrieMapWrapper typeAndAndTries;
    private final CompletionFinder completionFinder;
    private final OnComponentMetadataEvent onComponentMetadataEvent;
    private final ComponentInputDescriptorBuilder inputDescriptorBuilder;
    private final PlatformComponentService componentService;

    public PlatformComponentMetadataService(@NotNull Module module,
                                            @NotNull CompletionFinder completionFinder,
                                            @NotNull TrieMapWrapper typesMap,
                                            @NotNull PlatformComponentService componentService) {
        this.module = module;
        this.typeAndAndTries = typesMap;
        this.completionFinder = completionFinder;
        this.componentService = componentService;
        this.inputDescriptorBuilder = new ComponentInputDescriptorBuilder(componentService);
        onComponentMetadataEvent = module.getProject().getMessageBus().syncPublisher(Topics.ON_COMPONENT_IO);
    }

    ComponentOutputDescriptor componentOutputOf(ComponentContext context) {
        return DiscoveryStrategyFactory.get(module, componentService, typeAndAndTries, context, context.node())
                .orElse(null);
    }

    @Override
    public void componentMetadataOf(@NotNull ComponentContext context) {
        PluginExecutors.run(module, message("component.io.ticker.text"), indicator -> {
            try {
                Optional<? extends ComponentOutputDescriptor> componentOutputDescriptor =
                        DiscoveryStrategyFactory.get(module, componentService, typeAndAndTries, context, context.node());

                ComponentMetadataActualInputDTO actualInput = componentOutputDescriptor.map(descriptor -> {
                    if (descriptor instanceof MultipleMessages) {
                        return new ComponentMetadataActualInputDTO();
                    } else {
                        String description = descriptor.getDescription();
                        MetadataTypeDTO outputAttributes = attributes(descriptor);
                        List<MetadataTypeDTO> outputPayload = payload(descriptor);
                        return new ComponentMetadataActualInputDTO(outputAttributes, outputPayload, description);
                    }
                }).orElse(null);

                Optional<ComponentInputDescriptor> componentInputDescriptor = inputDescriptorBuilder.build(context);
                ComponentMetadataExpectedInputDTO expectedInput = componentInputDescriptor.map(descriptor -> {
                    List<String> payload = descriptor.getPayload();
                    String description = descriptor.getDescription();
                    return new ComponentMetadataExpectedInputDTO(payload, description);
                }).orElse(null);

                ComponentMetadataDTO componentMetadataDTO = new ComponentMetadataDTO(actualInput, expectedInput);

                onComponentMetadataEvent.onComponentMetadataUpdated(componentMetadataDTO);

            } catch (Exception exception) {
                String componentFullyQualifiedName = context.node().componentData().getFullyQualifiedName();
                String errorMessage =
                        format("Component metadata could not be found for component=[%s], cause=[%s]", componentFullyQualifiedName, exception);
                PluginException wrapped = new PluginException(errorMessage, exception);
                onComponentMetadataEvent.onComponentMetadataError(wrapped);
            }
        });
    }

    public MetadataTypeDTO attributes(ComponentOutputDescriptor output) {
        String attributeType = output != null ? output.getAttributes() : MessageAttributes.class.getName();
        Collection<MetadataTypeItemDTO> suggestions = findAndMapDTO(output, attributeType);
        return new MetadataTypeDTO(MessageAttributes.class.getName(), suggestions);
    }

    public List<MetadataTypeDTO> payload(ComponentOutputDescriptor output) {
        List<String> outputPayloadTypes = output != null ? output.getPayload() : singletonList(Object.class.getName());
        return outputPayloadTypes
                .stream()
                .map(outputType -> map(output, outputType))
                .collect(toList());
    }

    private MetadataTypeDTO map(ComponentOutputDescriptor output, String type) {
        Trie typeTrie = typeAndAndTries.getOrDefault(type, Default.UNKNOWN);
        if (isNotBlank(typeTrie.listItemType())) {
            // It is  a List type, we need to find the suggestions for the List item type.
            // The list type display is: List<FileType> : FileType
            String typeDisplay = PresentableTypeUtils.formatListDisplayType(type, typeTrie);
            String listItemType = typeTrie.listItemType();
            Collection<MetadataTypeItemDTO> typeDTOs = findAndMapDTO(output, listItemType);
            return new MetadataTypeDTO(typeDisplay, typeDTOs);

        } else {
            String typeDisplay = PresentableTypeUtils.from(type, typeTrie);
            Collection<MetadataTypeItemDTO> typeDTOs = findAndMapDTO(output, typeTrie);
            return new MetadataTypeDTO(typeDisplay, typeDTOs);
        }
    }

    private Collection<MetadataTypeItemDTO> findAndMapDTO(ComponentOutputDescriptor output, String type) {
        Trie typeTrie = typeAndAndTries.getOrDefault(type, Default.UNKNOWN);
        return findAndMapDTO(output, typeTrie);
    }

    private Collection<MetadataTypeItemDTO> findAndMapDTO(ComponentOutputDescriptor output, Trie typeTrie) {
        return find(typeTrie, output)
                .stream()
                .sorted(Comparator.comparing(Suggestion::getLookup))
                .map(mapper(output))
                .collect(toList());
    }

    public Function<Suggestion, MetadataTypeItemDTO> mapper(ComponentOutputDescriptor output) {
        return suggestion -> {
            String type = suggestion.getReturnType();
            Trie typeTrie = typeAndAndTries.getOrDefault(type, Default.UNKNOWN);
            if (isNotBlank(typeTrie.listItemType())) {
                // Unroll the list type
                String listItemType = typeTrie.listItemType();
                Trie listItemTrie = typeAndAndTries.getOrDefault(listItemType, Default.UNKNOWN);
                // The list type display is: List<FileType> : FileType
                String typeDisplay = PresentableTypeUtils.formatListDisplayType(type, typeTrie);
                return asTypeDTO(output, listItemType, listItemTrie,
                        suggestion.getLookup(),
                        typeDisplay);
            } else {
                return asTypeDTO(output, type, typeTrie,
                        suggestion.getLookup(),
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
