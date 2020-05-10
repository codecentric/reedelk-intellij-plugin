package com.reedelk.plugin.service.module.impl.component;

import com.intellij.openapi.module.Module;
import com.reedelk.module.descriptor.model.component.ComponentDescriptor;
import com.reedelk.module.descriptor.model.component.ComponentOutputDescriptor;
import com.reedelk.plugin.commons.Topics;
import com.reedelk.plugin.editor.properties.context.ContainerContext;
import com.reedelk.plugin.executor.PluginExecutors;
import com.reedelk.plugin.service.module.PlatformModuleService;
import com.reedelk.plugin.service.module.impl.component.completion.*;
import com.reedelk.plugin.service.module.impl.component.componentio.IOComponent;
import com.reedelk.plugin.service.module.impl.component.componentio.IOTypeDescriptor;
import com.reedelk.plugin.service.module.impl.component.componentio.IOTypeItem;
import com.reedelk.plugin.service.module.impl.component.componentio.OnComponentIO;
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

public class PlatformComponentIOServiceImpl implements PlatformModuleService {

    private final Module module;
    private OnComponentIO onComponentIO;
    private final TrieMapWrapper typeAndAndTries;
    private final CompletionFinder completionFinder;
    private final PlatformComponentServiceImpl componentTracker;

    public PlatformComponentIOServiceImpl(Module module, CompletionFinder completionFinder, TrieMapWrapper typesMap, PlatformComponentServiceImpl componentTracker) {
        this.module = module;
        this.componentTracker = componentTracker;
        this.completionFinder = completionFinder;
        this.typeAndAndTries = typesMap;
        onComponentIO = module.getProject().getMessageBus().syncPublisher(Topics.ON_COMPONENT_IO);
    }

    @Override
    public void inputOutputOf(ContainerContext context, String outputFullyQualifiedName) {
        PluginExecutors.run(module, "Fetching IO", indicator -> {
            String predecessorFQCN = context.predecessor();
            ComponentDescriptor componentDescriptorBy = componentTracker.componentDescriptorFrom(predecessorFQCN);
            ComponentOutputDescriptor output = componentDescriptorBy.getOutput();

            IOTypeDescriptor outputAttributes = attributes(output);
            List<IOTypeDescriptor> outputPayload = payload(output);
            IOComponent IOComponent = new IOComponent(outputAttributes, outputPayload);
            onComponentIO.onComponentIO(predecessorFQCN, outputFullyQualifiedName, IOComponent);
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
                .map(outputType -> asIOTypeDescriptor(output, outputType))
                .collect(toList());
    }

    @NotNull
    private IOTypeDescriptor asIOTypeDescriptor(ComponentOutputDescriptor output, String type) {
        Trie typeTrie = typeAndAndTries.getOrDefault(type, TrieUnknownType.get());
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

    @NotNull
    private Collection<Suggestion> find(Trie trie, ComponentOutputDescriptor output) {
        return completionFinder.find(trie, new String[]{EMPTY}, output)
                .stream()
                .filter(suggestion -> PROPERTY.equals(suggestion.getType()))
                .collect(toList());
    }

    @NotNull
    private Collection<IOTypeItem> findAndMapDTO(ComponentOutputDescriptor output, String type) {
        Trie typeTrie = typeAndAndTries.getOrDefault(type, TrieUnknownType.get());
        return findAndMapDTO(output, typeTrie);
    }

    @NotNull
    private Collection<IOTypeItem> findAndMapDTO(ComponentOutputDescriptor output, Trie typeTrie) {
        return find(typeTrie, output)
                .stream()
                .sorted(Comparator.comparing(Suggestion::lookupString))
                .map(mapper(output))
                .collect(toList());
    }

    public Function<Suggestion, IOTypeItem> mapper(ComponentOutputDescriptor output) {
        return suggestion -> {
            String suggestionType = suggestion.typeText();
            Trie typeTrie = typeAndAndTries.getOrDefault(suggestionType, TrieUnknownType.get());
            if (isNotBlank(typeTrie.listItemType())) {
                String listItemType = typeTrie.listItemType();
                Trie listItemTrie = typeAndAndTries.getOrDefault(listItemType, TrieUnknownType.get());
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
    private IOTypeItem asTypeDTO(ComponentOutputDescriptor output, String suggestionType, Trie orDefault, String propertyName, String propertyType) {
        Collection<Suggestion> suggestions = find(orDefault, output);
        if (suggestions.isEmpty()) {
            return new IOTypeItem(propertyName, propertyType);
        } else {
            // Complex type
            String finalPropertyName = propertyName + " : " + propertyType;
            IOTypeDescriptor ioTypeDescriptor = asIOTypeDescriptor(output, suggestionType);
            return new IOTypeItem(finalPropertyName, ioTypeDescriptor);
        }
    }
}
