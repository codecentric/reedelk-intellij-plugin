package com.reedelk.plugin.component.domain;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.reedelk.runtime.commons.JsonParser.Implementor;

/**
 * Decorator which adds the default "Description" property to all registered components.
 */
public class ComponentDescriptionDecorator implements ComponentDescriptor {

    private static final String DESCRIPTION_PROPERTY_DISPLAY_NAME = "Description";

    private ComponentDescriptor wrapped;
    private final ComponentPropertyDescriptor descriptionDescriptor;

    public ComponentDescriptionDecorator(ComponentDescriptor descriptor) {
        this.wrapped = descriptor;

        TypePrimitiveDescriptor typeDescriptor = new TypePrimitiveDescriptor(String.class);

        descriptionDescriptor = ComponentPropertyDescriptor.builder()
                .propertyName(Implementor.description())
                .type(typeDescriptor)
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
    public ComponentClass getComponentClass() {
        return wrapped.getComponentClass();
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
    public List<ComponentPropertyDescriptor> getPropertiesDescriptors() {
        List<ComponentPropertyDescriptor> descriptionDescriptorList = new ArrayList<>();
        descriptionDescriptorList.add(descriptionDescriptor);

        List<ComponentPropertyDescriptor> propertiesDescriptors = wrapped.getPropertiesDescriptors();
        descriptionDescriptorList.addAll(propertiesDescriptors);

        return Collections.unmodifiableList(descriptionDescriptorList);
    }

    @Override
    public Optional<ComponentPropertyDescriptor> getPropertyDescriptor(String propertyName) {
        if (propertyName.equals(Implementor.description())) {
            return Optional.of(descriptionDescriptor);
        } else {
            return wrapped.getPropertyDescriptor(propertyName);
        }
    }
}
