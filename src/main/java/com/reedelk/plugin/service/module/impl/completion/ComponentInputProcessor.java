package com.reedelk.plugin.service.module.impl.completion;

import com.reedelk.module.descriptor.model.component.ComponentOutputDescriptor;
import com.reedelk.runtime.api.commons.ImmutableMap;
import com.reedelk.runtime.api.commons.StringUtils;
import com.reedelk.runtime.api.message.MessageAttributes;

import java.util.*;

import static com.reedelk.plugin.service.module.impl.completion.Suggestion.Type.PROPERTY;

public class ComponentInputProcessor {

    private final Map<String, TypeInfo> typeAndAndTries;

    public ComponentInputProcessor(Map<String, TypeInfo> typeAndAndTries) {
        this.typeAndAndTries = typeAndAndTries;
    }

    public Optional<ComponentIO> inputFrom(ComponentOutputDescriptor output) {
        if (output != null) {
            String attributesTypes = output.getAttributes(); // Attributes type.

            // Type types
            Map<String, ComponentIO.IOTypeDescriptor> map = extractPropertiesFrom(attributesTypes);

            // Extends (such as MessageAttributes)
            String extendsType = typeAndAndTries.get(attributesTypes).getExtendsType();
            Map<String, ComponentIO.IOTypeDescriptor> extendsMap = extractPropertiesFrom(extendsType);

            extendsMap.putAll(map);

            return Optional.of(new ComponentIO(extendsMap, ImmutableMap.of()));

        } else {
            // Default attributes
            Map<String, ComponentIO.IOTypeDescriptor> map = extractPropertiesFrom(MessageAttributes.class.getName());

            return Optional.of(new ComponentIO(map, ImmutableMap.of()));
        }
    }

    private Map<String, ComponentIO.IOTypeDescriptor> extractPropertiesFrom(String type) {
        TypeInfo info = typeAndAndTries.get(type);
        List<Suggestion> attributesItems = info.getTrie().autocomplete(StringUtils.EMPTY); // All for the type
        Map<String, ComponentIO.IOTypeDescriptor> map = new TreeMap<>(Comparator.naturalOrder());
        attributesItems.stream()
                .filter(suggestion -> suggestion.getType().equals(PROPERTY))
                .forEach(suggestion -> map.put(suggestion.lookupString(),
                        ComponentIO.IOTypeDescriptor.create(suggestion.presentableType())));
        return map;
    }
}
