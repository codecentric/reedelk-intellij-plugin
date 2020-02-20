package com.reedelk.plugin.component;

import com.reedelk.module.descriptor.model.ComponentDescriptor;
import com.reedelk.module.descriptor.model.ComponentType;
import com.reedelk.module.descriptor.model.PropertyDescriptor;
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
    private final PropertyDescriptor descriptionDescriptor;
    private final List<PropertyDescriptor> componentPropertyDescriptorsWithDescription;

    public ComponentDescriptionDecorator(ComponentDescriptor descriptor) {
        this.wrapped = descriptor;

        TypePrimitiveDescriptor typeDescriptor = new TypePrimitiveDescriptor();
        typeDescriptor.setType(String.class);

        descriptionDescriptor = PropertyDescriptor.builder()
                .type(typeDescriptor)
                .name(Implementor.description())
                .hintValue(message("component.description.hint"))
                .displayName(DESCRIPTION_PROPERTY_DISPLAY_NAME)
                .initValue(wrapped.getDisplayName())
                .build();

        // The component properties are the union of the existing one + the description property.
        List<PropertyDescriptor> descriptionDescriptorList = new ArrayList<>();
        descriptionDescriptorList.add(descriptionDescriptor);
        List<PropertyDescriptor> propertiesDescriptors = wrapped.getProperties();
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
    public ComponentType getType() {
        return wrapped.getType();
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
    public List<PropertyDescriptor> getProperties() {
        return componentPropertyDescriptorsWithDescription;
    }

    @Override
    public Optional<PropertyDescriptor> getPropertyDescriptor(String propertyName) {
        if (propertyName.equals(Implementor.description())) {
            return Optional.of(descriptionDescriptor);
        } else {
            return wrapped.getPropertyDescriptor(propertyName);
        }
    }
}
