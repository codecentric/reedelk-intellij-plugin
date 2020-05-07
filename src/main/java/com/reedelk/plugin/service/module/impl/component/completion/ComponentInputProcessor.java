package com.reedelk.plugin.service.module.impl.component.completion;

import com.reedelk.module.descriptor.model.component.ComponentOutputDescriptor;
import com.reedelk.runtime.api.message.MessageAttributes;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.reedelk.plugin.service.module.impl.component.completion.Suggestion.Type.PROPERTY;
import static java.util.stream.Collectors.toList;

public class ComponentInputProcessor {

    private static final Trie UNKNOWN_TYPE_TRIE = new TrieDefault();
    private final TrieMapWrapper typeAndAndTries;

    public ComponentInputProcessor(TrieMapWrapper typeAndAndTries) {
        this.typeAndAndTries = typeAndAndTries;
    }

    public ComponentIO.OutputDescriptor outputAttributesFrom(ComponentOutputDescriptor output) {
        String attributeType = output != null ?
                output.getAttributes() : MessageAttributes.class.getName();
        Collection<Suggestion> suggestions = getSuggestions(output, attributeType);
        return new ComponentIO.OutputDescriptor(attributeType, suggestions);
    }

    @NotNull
    private Collection<Suggestion> getSuggestions(ComponentOutputDescriptor output, String attributesTypes) {
        Trie orDefault = typeAndAndTries.getOrDefault(attributesTypes, UNKNOWN_TYPE_TRIE);
        Collection<Suggestion> suggestions = CompletionFinder.find(orDefault, typeAndAndTries, output, new String[]{""});
        return suggestions.stream().filter(suggestion -> {
            return suggestion.getType().equals(PROPERTY); // We only want properties in the component INput output panel.
        }).sorted(Comparator.comparing(Suggestion::lookupString)).collect(toList());
    }

    public List<ComponentIO.OutputDescriptor> outputPayloadFrom(ComponentOutputDescriptor output) {
        List<String> outputPayloadTypes = output != null ?
                output.getPayload() : Collections.singletonList(Object.class.getName());
        return outputPayloadTypes.stream().map(outputType -> {
            Collection<Suggestion> suggestions = getSuggestions(output, outputType);
            return new ComponentIO.OutputDescriptor(outputType, suggestions);
        }).collect(toList());

    }
}
