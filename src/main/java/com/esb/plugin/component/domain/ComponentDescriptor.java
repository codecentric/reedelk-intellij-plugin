package com.esb.plugin.component.domain;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.util.List;
import java.util.Optional;

/**
 * Interface describing the component to be added to the graph.
 */
public interface ComponentDescriptor {

    DataFlavor FLAVOR = new DataFlavor(ComponentDescriptor.class, "Descriptor of a component");

    Icon getIcon();

    Image getImage();

    boolean isHidden();

    String getDisplayName();

    String getFullyQualifiedName();

    List<ComponentPropertyDescriptor> getPropertiesDescriptors();

    Optional<ComponentPropertyDescriptor> getPropertyDescriptor(String propertyName);

}

