package com.esb.plugin.component.domain;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.util.List;
import java.util.*;

/**
 * An object describing the component to be added to the graph.
 */
public class ComponentDescriptor {

    public static final DataFlavor FLAVOR = new DataFlavor(ComponentDescriptor.class, "Descriptor of ComponentData");

    private boolean hidden;

    private Icon icon;
    private Image image;

    private String displayName;
    private String fullyQualifiedName;
    private List<ComponentPropertyDescriptor> componentPropertyDescriptors = new ArrayList<>();

    protected ComponentDescriptor() {
    }

    public boolean isHidden() {
        return hidden;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getFullyQualifiedName() {
        return fullyQualifiedName;
    }

    public Optional<ComponentPropertyDescriptor> getPropertyDescriptor(String propertyName) {
        return componentPropertyDescriptors
                .stream()
                .filter(descriptor -> descriptor.getPropertyName().equals(propertyName))
                .findFirst();
    }

    public List<ComponentPropertyDescriptor> getPropertiesDescriptors() {
        return Collections.unmodifiableList(componentPropertyDescriptors);
    }

    public Icon getIcon() {
        return icon;
    }

    public Image getImage() {
        return image;
    }

    public static Builder create() {
        return new Builder();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ComponentDescriptor that = (ComponentDescriptor) o;
        return fullyQualifiedName.equals(that.fullyQualifiedName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fullyQualifiedName);
    }

    public static class Builder {

        private boolean hidden;

        private Icon icon;
        private Image image;
        private String displayName;
        private String fullyQualifiedName;
        private List<ComponentPropertyDescriptor> componentPropertyDescriptors = new ArrayList<>();

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
            ComponentDescriptor descriptor = new ComponentDescriptor();
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
