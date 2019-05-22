package com.esb.plugin.component.unknown;

import com.esb.plugin.component.ComponentDescriptor;
import com.esb.plugin.component.ComponentPropertyDescriptor;

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
    public String getFullyQualifiedName() {
        return wrapped.getFullyQualifiedName();
    }

    @Override
    public String getDisplayName() {
        return wrapped.getDisplayName();
    }

    @Override
    public boolean isHidden() {
        return wrapped.isHidden();
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
    public List<ComponentPropertyDescriptor> getPropertiesDescriptors() {
        return wrapped.getPropertiesDescriptors();
    }

    @Override
    public Optional<ComponentPropertyDescriptor> getPropertyDescriptor(String propertyName) {
        return Optional.of(new UnknownComponentPropertyDescriptor(propertyName));
    }

    class UnknownComponentPropertyDescriptor extends ComponentPropertyDescriptor {
        UnknownComponentPropertyDescriptor(String propertyName) {
            super(propertyName, new UnknownPropertyType(), "Unknown", null, false);
        }
    }

}
