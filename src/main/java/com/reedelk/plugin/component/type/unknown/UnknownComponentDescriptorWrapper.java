package com.reedelk.plugin.component.type.unknown;

import com.reedelk.module.descriptor.model.component.ComponentDescriptor;
import com.reedelk.module.descriptor.model.component.ComponentType;
import com.reedelk.module.descriptor.model.property.PropertyDescriptor;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Optional;

public class UnknownComponentDescriptorWrapper extends ComponentDescriptor {

    private final ComponentDescriptor wrapped;

    public UnknownComponentDescriptorWrapper(ComponentDescriptor wrapped) {
        this.wrapped = wrapped;
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
    public String getDisplayName() {
        return wrapped.getDisplayName();
    }

    @Override
    public ComponentType getType() {
        return wrapped.getType();
    }

    @Override
    public String getFullyQualifiedName() {
        return wrapped.getFullyQualifiedName();
    }

    @Override
    public List<PropertyDescriptor> getProperties() {
        return wrapped.getProperties();
    }

    @Override
    public Optional<PropertyDescriptor> getPropertyDescriptor(String propertyName) {
        return Optional.of(PropertyDescriptor
                .builder()
                .name(propertyName)
                .type(new UnknownPropertyType())
                .displayName("Unknown")
                .build());
    }
}
