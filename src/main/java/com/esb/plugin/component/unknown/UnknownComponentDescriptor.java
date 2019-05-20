package com.esb.plugin.component.unknown;

import com.esb.component.Unknown;
import com.esb.plugin.component.ComponentDescriptor;
import com.esb.plugin.component.ComponentPropertyDescriptor;

import java.util.Optional;

public class UnknownComponentDescriptor extends ComponentDescriptor {

    public UnknownComponentDescriptor() {
    }

    @Override
    public String getFullyQualifiedName() {
        return Unknown.class.getName();
    }

    @Override
    public String getDisplayName() {
        return "Unknown Component";
    }

    @Override
    public Optional<ComponentPropertyDescriptor> getPropertyDescriptor(String propertyName) {
        return Optional.of(new UnknownComponentPropertyDescriptor(propertyName));
    }

}
