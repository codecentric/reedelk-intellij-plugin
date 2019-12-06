package com.reedelk.plugin.component.domain;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Optional;

/**
 * Interface describing the component to be added to the graph.
 */
public interface ComponentDescriptor {

    Icon getIcon();

    Image getImage();

    boolean isHidden();

    String getDisplayName();

    String getFullyQualifiedName();

    ComponentType getComponentType();

    List<ComponentPropertyDescriptor> getPropertiesDescriptors();

    Optional<ComponentPropertyDescriptor> getPropertyDescriptor(String propertyName);

}

