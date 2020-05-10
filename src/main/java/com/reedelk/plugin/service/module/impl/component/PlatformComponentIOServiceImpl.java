package com.reedelk.plugin.service.module.impl.component;

import com.intellij.openapi.module.Module;
import com.reedelk.module.descriptor.model.component.ComponentDescriptor;
import com.reedelk.module.descriptor.model.component.ComponentOutputDescriptor;
import com.reedelk.plugin.commons.Topics;
import com.reedelk.plugin.editor.properties.context.ContainerContext;
import com.reedelk.plugin.executor.PluginExecutors;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.service.module.PlatformModuleService;
import com.reedelk.plugin.service.module.impl.component.completion.*;
import com.reedelk.plugin.service.module.impl.component.componentio.IOComponent;
import com.reedelk.plugin.service.module.impl.component.componentio.IOTypeDescriptor;
import com.reedelk.plugin.service.module.impl.component.componentio.IOTypeItem;
import com.reedelk.plugin.service.module.impl.component.componentio.OnComponentIO;
import com.reedelk.runtime.api.annotation.ComponentOutput;
import com.reedelk.runtime.api.commons.StringUtils;
import com.reedelk.runtime.api.message.MessageAttributes;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

import static com.reedelk.plugin.message.ReedelkBundle.message;
import static com.reedelk.plugin.service.module.impl.component.completion.Suggestion.Type.PROPERTY;
import static com.reedelk.runtime.api.commons.StringUtils.isNotBlank;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

public class PlatformComponentIOServiceImpl implements PlatformModuleService {

    private final Module module;
    private final OnComponentIO onComponentIO;
    private final TrieMapWrapper typeAndAndTries;
    private final CompletionFinder completionFinder;
    private final PlatformComponentServiceImpl componentService;

    public PlatformComponentIOServiceImpl(@NotNull Module module,
                                          @NotNull CompletionFinder completionFinder,
                                          @NotNull TrieMapWrapper typesMap,
                                          @NotNull PlatformComponentServiceImpl componentService) {
        this.module = module;
        this.typeAndAndTries = typesMap;
        this.componentService = componentService;
        this.completionFinder = completionFinder;
        onComponentIO = module.getProject().getMessageBus().syncPublisher(Topics.ON_COMPONENT_IO);
    }

    // TODO: Catch any exception they are eaten up!
    @Override
    public void inputOutputOf(ContainerContext context, String componentFullyQualifiedName) {
        PluginExecutors.run(module, message("component.io.ticker.text"), indicator -> {
            GraphNode predecessorNode = context.predecessor();

            String fullyQualifiedName = predecessorNode.componentData().getFullyQualifiedName();
            ComponentDescriptor componentDescriptorBy = componentService.componentDescriptorFrom(fullyQualifiedName);
            ComponentOutputDescriptor output = componentDescriptorBy.getOutput();


            if (output != null && output.getPayload().contains(ComponentOutput.PreviousComponent.class.getName())) {
                // We must take the payload from the previous component.
                GraphNode predecessor = context.predecessor(predecessorNode);
                String newFullyQualifiedName = predecessor.componentData().getFullyQualifiedName();
                ComponentDescriptor newComponentDescriptor = componentService.componentDescriptorFrom(newFullyQualifiedName);
                ComponentOutputDescriptor newOutput = newComponentDescriptor.getOutput();
                output = new ComponentOutputDescriptor();
                output.setDescription(newOutput.getDescription());
                output.setPayload(newOutput.getPayload());
                output.setAttributes(newOutput.getAttributes());
            }

            IOTypeDescriptor outputAttributes = attributes(output);
            List<IOTypeDescriptor> outputPayload = payload(output);

            String description = output != null ? output.getDescription() : StringUtils.EMPTY;
            IOComponent IOComponent = new IOComponent(outputAttributes, outputPayload, description);
            onComponentIO.onComponentIO(fullyQualifiedName, componentFullyQualifiedName, IOComponent);
        });
    }

    public IOTypeDescriptor attributes(ComponentOutputDescriptor output) {
        String attributeType = output != null ? output.getAttributes() : MessageAttributes.class.getName();
        Collection<IOTypeItem> suggestions = findAndMapDTO(output, attributeType);
        return new IOTypeDescriptor(MessageAttributes.class.getName(), suggestions);
    }

    public List<IOTypeDescriptor> payload(ComponentOutputDescriptor output) {
        List<String> outputPayloadTypes = output != null ? output.getPayload() : singletonList(Object.class.getName());
        return outputPayloadTypes
                .stream()
                .map(outputType -> map(output, outputType))
                .collect(toList());
    }

    private IOTypeDescriptor map(ComponentOutputDescriptor output, String type) {
        Trie typeTrie = typeAndAndTries.getOrDefault(type, Default.UNKNOWN);
        if (isNotBlank(typeTrie.listItemType())) {
            // It is  a List type, we need to find the suggestions for the List item type.
            // The list type display is: List<FileType> : FileType
            String typeDisplay = PresentableTypeUtils.formatListDisplayType(type, typeTrie);
            String listItemType = typeTrie.listItemType();
            Collection<IOTypeItem> typeDTOs = findAndMapDTO(output, listItemType);
            return new IOTypeDescriptor(typeDisplay, typeDTOs);

        } else {
            String typeDisplay = PresentableTypeUtils.from(type, typeTrie);
            Collection<IOTypeItem> typeDTOs = findAndMapDTO(output, typeTrie);
            return new IOTypeDescriptor(typeDisplay, typeDTOs);
        }
    }

    private Collection<IOTypeItem> findAndMapDTO(ComponentOutputDescriptor output, String type) {
        Trie typeTrie = typeAndAndTries.getOrDefault(type, Default.UNKNOWN);
        return findAndMapDTO(output, typeTrie);
    }

    private Collection<IOTypeItem> findAndMapDTO(ComponentOutputDescriptor output, Trie typeTrie) {
        return find(typeTrie, output)
                .stream()
                .sorted(Comparator.comparing(Suggestion::lookupString))
                .map(mapper(output))
                .collect(toList());
    }

    public Function<Suggestion, IOTypeItem> mapper(ComponentOutputDescriptor output) {
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

    private IOTypeItem asTypeDTO(ComponentOutputDescriptor output, String type, Trie typeTrie, String propertyName, String propertyType) {
        Collection<Suggestion> suggestions = find(typeTrie, output);
        if (suggestions.isEmpty()) {
            // Simple name/value
            return new IOTypeItem(propertyName, propertyType);
        } else {
            // Complex type
            String finalPropertyName = propertyName + " : " + propertyType;
            IOTypeDescriptor ioTypeDescriptor = map(output, type);
            return new IOTypeItem(finalPropertyName, ioTypeDescriptor);
        }
    }

    private Collection<Suggestion> find(Trie trie, ComponentOutputDescriptor output) {
        return completionFinder.find(trie, output)
                .stream()
                .filter(suggestion -> PROPERTY.equals(suggestion.getType()))
                .collect(toList());
    }
}
