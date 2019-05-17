package com.esb.plugin;

import com.esb.plugin.component.ComponentDescriptor;
import com.esb.plugin.component.ComponentPropertyDescriptor;
import com.esb.plugin.component.PrimitiveTypeDescriptor;
import com.esb.plugin.component.TypeDescriptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.singletonList;

public class ComponentDescriptorDecorator extends ComponentDescriptor {

    public static final String DESCRIPTION_PROPERTY_NAME = "description";

    private ComponentDescriptor wrapped;
    private final ComponentPropertyDescriptor descriptionDescriptor;

    public ComponentDescriptorDecorator(ComponentDescriptor descriptor) {
        this.wrapped = descriptor;

        PrimitiveTypeDescriptor typeDescriptor = new PrimitiveTypeDescriptor(String.class);
        descriptionDescriptor = new ComponentPropertyDescriptor(
                DESCRIPTION_PROPERTY_NAME, "Description", false, wrapped.getDisplayName(), typeDescriptor);
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
    public Optional<ComponentPropertyDescriptor> getPropertyDefinition(String propertyName) {
        if (propertyName.equals(DESCRIPTION_PROPERTY_NAME)) {
            return Optional.of(descriptionDescriptor);
        } else {
            return wrapped.getPropertyDefinition(propertyName);
        }
    }

    @Override
    public List<String> getPropertiesNames() {
        List<String> allProperties = new ArrayList<>(singletonList(DESCRIPTION_PROPERTY_NAME));
        allProperties.addAll(wrapped.getPropertiesNames());
        return allProperties;
    }

    @Override
    public List<ComponentPropertyDescriptor> getComponentPropertyDescriptors() {
        List<ComponentPropertyDescriptor> componentPropertyDescriptors =
                new ArrayList<>(singletonList(descriptionDescriptor));
        componentPropertyDescriptors.addAll(wrapped.getComponentPropertyDescriptors());
        return componentPropertyDescriptors;
    }

    @Override
    public TypeDescriptor getPropertyType(String propertyName) {
        if (propertyName.equals(DESCRIPTION_PROPERTY_NAME)) {
            return descriptionDescriptor.getPropertyType();
        } else {
            return wrapped.getPropertyType(propertyName);
        }
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
