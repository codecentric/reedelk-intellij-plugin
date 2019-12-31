package com.reedelk.plugin.component;

import com.reedelk.module.descriptor.model.ComponentDescriptor;
import com.reedelk.module.descriptor.model.ComponentPropertyDescriptor;
import com.reedelk.module.descriptor.model.ComponentType;
import com.reedelk.module.descriptor.model.TypePrimitiveDescriptor;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.reedelk.plugin.message.ReedelkBundle.message;
import static com.reedelk.runtime.commons.JsonParser.Implementor;
import static java.util.Collections.unmodifiableList;

/**
 * Decorator which adds the default "Description" property to all registered components.
 */
public class ComponentDescriptionDecorator extends ComponentDescriptor {

    private static final String DESCRIPTION_PROPERTY_DISPLAY_NAME = "Description";

    private ComponentDescriptor wrapped;
    private final ComponentPropertyDescriptor descriptionDescriptor;
    private final List<ComponentPropertyDescriptor> componentPropertyDescriptorsWithDescription;

    public ComponentDescriptionDecorator(ComponentDescriptor descriptor) {
        this.wrapped = descriptor;

        TypePrimitiveDescriptor typeDescriptor = new TypePrimitiveDescriptor();
        typeDescriptor.setType(String.class);

        descriptionDescriptor = ComponentPropertyDescriptor.builder()
                .propertyName(Implementor.description())
                .type(typeDescriptor)
                .hintValue(message("component.description.hint"))
                .displayName(DESCRIPTION_PROPERTY_DISPLAY_NAME)
                .defaultValue(wrapped.getDisplayName())
                .build();

        // The component properties are the union of the existing one + the description property.
        List<ComponentPropertyDescriptor> descriptionDescriptorList = new ArrayList<>();
        descriptionDescriptorList.add(descriptionDescriptor);
        List<ComponentPropertyDescriptor> propertiesDescriptors = wrapped.getComponentPropertyDescriptors();
        descriptionDescriptorList.addAll(propertiesDescriptors);
        this.componentPropertyDescriptorsWithDescription = unmodifiableList(descriptionDescriptorList);
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

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
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
        return componentPropertyDescriptorsWithDescription;
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