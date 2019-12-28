package com.reedelk.plugin.component;

import com.reedelk.plugin.component.domain.ComponentDescriptor;
import com.reedelk.plugin.component.domain.ComponentPropertyDescriptor;
import com.reedelk.plugin.component.domain.ComponentType;
import com.reedelk.plugin.component.domain.TypePrimitiveDescriptor;
import com.reedelk.runtime.commons.JsonParser;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.reedelk.plugin.message.ReedelkBundle.message;

/**
 * Decorator which adds the default "Description" property to all registered components.
 */
public class ComponentDescriptionDecorator extends ComponentDescriptor {

    private static final String DESCRIPTION_PROPERTY_DISPLAY_NAME = "Description";

    private ComponentDescriptor wrapped;
    private final ComponentPropertyDescriptor descriptionDescriptor;

    public ComponentDescriptionDecorator(ComponentDescriptor descriptor) {
        this.wrapped = descriptor;

        TypePrimitiveDescriptor typeDescriptor = new TypePrimitiveDescriptor();
        typeDescriptor.setType(String.class);

        descriptionDescriptor = ComponentPropertyDescriptor.builder()
                .propertyName(JsonParser.Implementor.description())
                .type(typeDescriptor)
                .hintValue(message("component.description.hint"))
                .displayName(DESCRIPTION_PROPERTY_DISPLAY_NAME)
                .defaultValue(wrapped.getDisplayName())
                .build();
    }

    @Override
    public Icon getIcon() {
        return wrapped.getIcon();
    }


    @Override
    public Image getImage() {
        return wrapped.getImage();
    }

    @Override
    public boolean isHidden() {
        return wrapped.isHidden();
    }

    @Override
    public ComponentType getComponentType() {
        return wrapped.getComponentType();
    }

    @Override
    public String getDisplayName() {
        return wrapped.getDisplayName();
    }

    @Override
    public String getFullyQualifiedName() {
        return wrapped.getFullyQualifiedName();
    }

    @Override
    public boolean equals(Object o) {
        return wrapped.equals(o);
    }

    @Override
    public int hashCode() {
        return wrapped.hashCode();
    }

    @Override
    public List<ComponentPropertyDescriptor> getComponentPropertyDescriptors() {
        List<ComponentPropertyDescriptor> descriptionDescriptorList = new ArrayList<>();
        descriptionDescriptorList.add(descriptionDescriptor);

        List<ComponentPropertyDescriptor> propertiesDescriptors = wrapped.getComponentPropertyDescriptors();
        descriptionDescriptorList.addAll(propertiesDescriptors);

        return Collections.unmodifiableList(descriptionDescriptorList);
    }

    @Override
    public Optional<ComponentPropertyDescriptor> getPropertyDescriptor(String propertyName) {
        if (propertyName.equals(JsonParser.Implementor.description())) {
            return Optional.of(descriptionDescriptor);
        } else {
            return wrapped.getPropertyDescriptor(propertyName);
        }
    }
}