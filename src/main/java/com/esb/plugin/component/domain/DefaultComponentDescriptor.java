package com.esb.plugin.component.domain;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class DefaultComponentDescriptor implements ComponentDescriptor {

    private Icon icon;
    private Image image;
    private boolean hidden;
    private String displayName;
    private String fullyQualifiedName;
    private List<ComponentPropertyDescriptor> componentPropertyDescriptors = new ArrayList<>();

    private DefaultComponentDescriptor() {
    }

    @Override
    public Icon getIcon() {
        return icon;
    }

    @Override
    public Image getImage() {
        return image;
    }

    @Override
    public boolean isHidden() {
        return hidden;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String getFullyQualifiedName() {
        return fullyQualifiedName;
    }

    @Override
    public List<ComponentPropertyDescriptor> getPropertiesDescriptors() {
        return Collections.unmodifiableList(componentPropertyDescriptors);
    }

    @Override
    public Optional<ComponentPropertyDescriptor> getPropertyDescriptor(String propertyName) {
        return componentPropertyDescriptors
                .stream()
                .filter(descriptor -> descriptor.getPropertyName().equals(propertyName))
                .findFirst();
    }

    public static Builder create() {
        return new Builder();
    }

    public static class Builder {

        private boolean hidden;

        private Icon icon;
        private Image image;
        private String displayName;
        private String fullyQualifiedName;
        private java.util.List<ComponentPropertyDescriptor> componentPropertyDescriptors = new ArrayList<>();

        public Builder propertyDefinitions(List<ComponentPropertyDescriptor> componentPropertyDescriptors) {
            this.componentPropertyDescriptors.addAll(componentPropertyDescriptors);
            return this;
        }

        public Builder fullyQualifiedName(String fullyQualifiedName) {
            this.fullyQualifiedName = fullyQualifiedName;
            return this;
        }

        public Builder displayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public Builder hidden(boolean hidden) {
            this.hidden = hidden;
            return this;
        }

        public Builder paletteIcon(Icon icon) {
            this.icon = icon;
            return this;
        }

        public Builder icon(Image image) {
            this.image = image;
            return this;
        }

        public ComponentDescriptor build() {
            DefaultComponentDescriptor descriptor = new DefaultComponentDescriptor();
            descriptor.hidden = hidden;
            descriptor.displayName = displayName;
            descriptor.icon = icon;
            descriptor.image = image;
            descriptor.fullyQualifiedName = fullyQualifiedName;
            descriptor.componentPropertyDescriptors.addAll(componentPropertyDescriptors);
            return descriptor;
        }
    }

}
