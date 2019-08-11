package com.reedelk.plugin.component.type.unknown;

import com.reedelk.plugin.component.domain.ComponentClass;
import com.reedelk.plugin.component.domain.ComponentDescriptor;
import com.reedelk.plugin.component.domain.ComponentPropertyDescriptor;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Optional;

public class UnknownComponentDescriptorWrapper implements ComponentDescriptor {

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
    public ComponentClass getComponentClass() {
        return wrapped.getComponentClass();
    }

    @Override
    public String getFullyQualifiedName() {
        return wrapped.getFullyQualifiedName();
    }

    @Override
    public List<ComponentPropertyDescriptor> getPropertiesDescriptors() {
        return wrapped.getPropertiesDescriptors();
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
