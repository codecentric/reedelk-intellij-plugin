package com.esb.plugin.component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Decorator which adds the default "Description" property to all registered components.
 */
public class ComponentDescriptionDecorator extends ComponentDescriptor {

    public static final String DESCRIPTION_PROPERTY_NAME = "description";

    private ComponentDescriptor wrapped;
    private final ComponentPropertyDescriptor descriptionDescriptor;

    public ComponentDescriptionDecorator(ComponentDescriptor descriptor) {
        this.wrapped = descriptor;

        PrimitiveTypeDescriptor typeDescriptor = new PrimitiveTypeDescriptor(String.class);
        descriptionDescriptor = new ComponentPropertyDescriptor(
                DESCRIPTION_PROPERTY_NAME, typeDescriptor, "Description", wrapped.getDisplayName(), false);
    }

    @Override
    public String getFullyQualifiedName() {
        return wrapped.getFullyQualifiedName();
    }

    @Override
    public String getDisplayName() {
        return wrapped.getDisplayName();
    }

    @Override
    public Optional<ComponentPropertyDescriptor> getPropertyDescriptor(String propertyName) {
        if (propertyName.equals(DESCRIPTION_PROPERTY_NAME)) {
            return Optional.of(descriptionDescriptor);
        } else {
            return wrapped.getPropertyDescriptor(propertyName);
        }
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
    public boolean equals(Object o) {
        return wrapped.equals(o);
    }

    @Override
    public int hashCode() {
        return wrapped.hashCode();
    }
}
