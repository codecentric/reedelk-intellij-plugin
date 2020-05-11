package com.reedelk.plugin.service.module.impl.component;

import com.intellij.openapi.module.Module;
import com.reedelk.module.descriptor.model.component.ComponentInputDescriptor;
import com.reedelk.module.descriptor.model.component.ComponentOutputDescriptor;
import com.reedelk.plugin.commons.Topics;
import com.reedelk.plugin.editor.properties.context.ContainerContext;
import com.reedelk.plugin.executor.PluginExecutors;
import com.reedelk.plugin.service.module.PlatformModuleService;
import com.reedelk.plugin.service.module.impl.component.completion.*;
import com.reedelk.plugin.service.module.impl.component.discovery.DiscoveryStrategyFactory;
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
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

public class PlatformComponentMetadataServiceImpl implements PlatformModuleService {

    private final Module module;
    private final TrieMapWrapper typeAndAndTries;
    private final CompletionFinder completionFinder;
    private final OnComponentMetadata onComponentMetadata;
    private final ComponentInputDescriptorBuilder inputDescriptorBuilder;
    private final PlatformComponentServiceImpl componentService;

    public PlatformComponentMetadataServiceImpl(@NotNull Module module,
                                                @NotNull CompletionFinder completionFinder,
                                                @NotNull TrieMapWrapper typesMap,
                                                @NotNull PlatformComponentServiceImpl componentService) {
        this.module = module;
        this.typeAndAndTries = typesMap;
        this.completionFinder = completionFinder;
        this.componentService = componentService;
        this.inputDescriptorBuilder = new ComponentInputDescriptorBuilder(componentService);
        onComponentMetadata = module.getProject().getMessageBus().syncPublisher(Topics.ON_COMPONENT_IO);
    }

    // TODO: Catch any exception they are eaten up!
    @Override
    public void componentMetadataOf(ContainerContext context) {
        PluginExecutors.run(module, message("component.io.ticker.text"), indicator -> {

            try {

                Optional<ComponentOutputDescriptor> componentOutputDescriptor = DiscoveryStrategyFactory.get(context, componentService);
                ComponentMetadataActualInput actualInput = componentOutputDescriptor.map(descriptor -> {
                    String description = descriptor.getDescription();
                    MetadataTypeDescriptor outputAttributes = attributes(descriptor);
                    List<MetadataTypeDescriptor> outputPayload = payload(descriptor);
                    return new ComponentMetadataActualInput(outputAttributes, outputPayload, description);
                }).orElse(null);

                Optional<ComponentInputDescriptor> componentInputDescriptor = inputDescriptorBuilder.build(context);
                ComponentMetadataExpectedInput expectedInput = componentInputDescriptor.map(descriptor -> {
                    List<String> payload = descriptor.getPayload();
                    String description = descriptor.getDescription();
                    return new ComponentMetadataExpectedInput(payload, description);
                }).orElse(null);

                ComponentMetadata componentMetadata = new ComponentMetadata(actualInput, expectedInput);

                onComponentMetadata.onComponentMetadata(componentMetadata);

            } catch (Exception exception) {
                // Extract current node fully qualified name
                onComponentMetadata.onComponentMetadataError("Component metadata could not be found for component, cause=[" + exception.getMessage() + "]");
            }
        });
    }

    public MetadataTypeDescriptor attributes(ComponentOutputDescriptor output) {
        String attributeType = output != null ? output.getAttributes() : MessageAttributes.class.getName();
        Collection<MetadataTypeItemDescriptor> suggestions = findAndMapDTO(output, attributeType);
        return new MetadataTypeDescriptor(MessageAttributes.class.getName(), suggestions);
    }

    public List<MetadataTypeDescriptor> payload(ComponentOutputDescriptor output) {
        List<String> outputPayloadTypes = output != null ? output.getPayload() : singletonList(Object.class.getName());
        return outputPayloadTypes
                .stream()
                .map(outputType -> map(output, outputType))
                .collect(toList());
    }

    private MetadataTypeDescriptor map(ComponentOutputDescriptor output, String type) {
        Trie typeTrie = typeAndAndTries.getOrDefault(type, Default.UNKNOWN);
        if (isNotBlank(typeTrie.listItemType())) {
            // It is  a List type, we need to find the suggestions for the List item type.
            // The list type display is: List<FileType> : FileType
            String typeDisplay = PresentableTypeUtils.formatListDisplayType(type, typeTrie);
            String listItemType = typeTrie.listItemType();
            Collection<MetadataTypeItemDescriptor> typeDTOs = findAndMapDTO(output, listItemType);
            return new MetadataTypeDescriptor(typeDisplay, typeDTOs);

        } else {
            String typeDisplay = PresentableTypeUtils.from(type, typeTrie);
            Collection<MetadataTypeItemDescriptor> typeDTOs = findAndMapDTO(output, typeTrie);
            return new MetadataTypeDescriptor(typeDisplay, typeDTOs);
        }
    }

    private Collection<MetadataTypeItemDescriptor> findAndMapDTO(ComponentOutputDescriptor output, String type) {
        Trie typeTrie = typeAndAndTries.getOrDefault(type, Default.UNKNOWN);
        return findAndMapDTO(output, typeTrie);
    }

    private Collection<MetadataTypeItemDescriptor> findAndMapDTO(ComponentOutputDescriptor output, Trie typeTrie) {
        return find(typeTrie, output)
                .stream()
                .sorted(Comparator.comparing(Suggestion::lookupString))
                .map(mapper(output))
                .collect(toList());
    }

    public Function<Suggestion, MetadataTypeItemDescriptor> mapper(ComponentOutputDescriptor output) {
        return suggestion -> {
            String type = suggestion.typeText();
            Trie typeTrie = typeAndAndTries.getOrDefault(type, Default.UNKNOWN);
            if (isNotBlank(typeTrie.listItemType())) {
                // Unroll the list type
                String listItemType = typeTrie.listItemType();
                Trie listItemTrie = typeAndAndTries.getOrDefault(listItemType, Default.UNKNOWN);
                // The list type display is: List<FileType> : FileType
                String typeDisplay = PresentableTypeUtils.formatListDisplayType(type, typeTrie);
                return asTypeDTO(output, listItemType, listItemTrie,
                        suggestion.lookupString(),
                        typeDisplay);
            } else {
                return asTypeDTO(output, type, typeTrie,
                        suggestion.lookupString(),
                        suggestion.presentableType());
            }
        };
    }

    private MetadataTypeItemDescriptor asTypeDTO(ComponentOutputDescriptor output, String type, Trie typeTrie, String propertyName, String propertyType) {
        Collection<Suggestion> suggestions = find(typeTrie, output);
        if (suggestions.isEmpty()) {
            // Simple name/value
            return new MetadataTypeItemDescriptor(propertyName, propertyType);
        } else {
            // Complex type
            String finalPropertyName = propertyName + " : " + propertyType;
            MetadataTypeDescriptor metadataTypeDescriptor = map(output, type);
            return new MetadataTypeItemDescriptor(finalPropertyName, metadataTypeDescriptor);
        }
    }

    private Collection<Suggestion> find(Trie trie, ComponentOutputDescriptor output) {
        return completionFinder.find(trie, output)
                .stream()
                .filter(suggestion -> PROPERTY.equals(suggestion.getType()))
                .collect(toList());
    }
}
