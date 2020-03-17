package com.reedelk.plugin.editor.properties.renderer.typemap.custom;

import com.intellij.openapi.module.Module;
import com.reedelk.module.descriptor.model.ComponentDataHolder;
import com.reedelk.module.descriptor.model.PropertyDescriptor;
import com.reedelk.module.descriptor.model.TypeDescriptor;
import com.reedelk.module.descriptor.model.TypeObjectDescriptor;
import com.reedelk.plugin.editor.properties.accessor.PropertyAccessor;
import com.reedelk.plugin.editor.properties.commons.ContainerFactory;
import com.reedelk.plugin.editor.properties.commons.DisposablePanel;
import com.reedelk.plugin.editor.properties.commons.PropertiesPanelHolder;
import com.reedelk.plugin.editor.properties.renderer.PropertyTypeRenderer;
import com.reedelk.plugin.editor.properties.renderer.PropertyTypeRendererFactory;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import static java.awt.BorderLayout.CENTER;

public class MapTableCustomObjectPanel extends DisposablePanel {

    public MapTableCustomObjectPanel(Module module, ComponentDataHolder dataHolder, TypeObjectDescriptor objectDescriptor) {
        List<PropertyDescriptor> descriptors = objectDescriptor.getObjectProperties();

        PropertiesPanelHolder propertiesPanel =
                new PropertiesPanelHolder(objectDescriptor.getTypeFullyQualifiedName(), dataHolder, descriptors);

        descriptors.forEach(propertyDescriptor -> {

            String propertyName = propertyDescriptor.getName();

            PropertyAccessor propertyAccessor = propertiesPanel.getAccessor(propertyName);

            TypeDescriptor propertyType = propertyDescriptor.getType();

            PropertyTypeRenderer renderer = PropertyTypeRendererFactory.get().from(propertyType);

            JComponent renderedComponent = renderer.render(module, propertyDescriptor, propertyAccessor, propertiesPanel);

            renderer.addToParent(propertiesPanel, renderedComponent, propertyDescriptor, propertiesPanel);

        });

        setLayout(new BorderLayout());
        add(ContainerFactory.pushTop(propertiesPanel), CENTER);
    }
}
