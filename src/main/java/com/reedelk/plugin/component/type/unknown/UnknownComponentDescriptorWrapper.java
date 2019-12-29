package com.reedelk.plugin.component.type.unknown;

import com.reedelk.component.descriptor.ComponentDescriptor;
import com.reedelk.component.descriptor.ComponentPropertyDescriptor;
import com.reedelk.component.descriptor.ComponentType;

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
    public ComponentType getComponentType() {
        return wrapped.getComponentType();
    }

    @Override
    public String getFullyQualifiedName() {
        return wrapped.getFullyQualifiedName();
    }

    @Override
    public List<ComponentPropertyDescriptor> getComponentPropertyDescriptors() {
        return wrapped.getComponentPropertyDescriptors();
    }

    @Override
    public Optional<ComponentPropertyDescriptor> getPropertyDescriptor(String propertyName) {
        return Optional.of(ComponentPropertyDescriptor
                .builder()
                .propertyName(propertyName)
                .type(new UnknownPropertyType())
                .displayName("Unknown")
                .build());
    }
}
