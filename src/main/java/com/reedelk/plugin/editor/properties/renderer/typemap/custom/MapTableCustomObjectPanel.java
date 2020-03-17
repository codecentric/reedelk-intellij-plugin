package com.reedelk.plugin.editor.properties.renderer.typemap.custom;

import com.intellij.openapi.module.Module;
import com.reedelk.module.descriptor.model.ComponentDataHolder;
import com.reedelk.module.descriptor.model.PropertyDescriptor;
import com.reedelk.module.descriptor.model.TypeDescriptor;
import com.reedelk.module.descriptor.model.TypeObjectDescriptor;
import com.reedelk.plugin.editor.properties.accessor.PropertyAccessor;
import com.reedelk.plugin.editor.properties.accessor.PropertyAccessorFactory;
import com.reedelk.plugin.editor.properties.commons.ContainerFactory;
import com.reedelk.plugin.editor.properties.commons.DisposablePanel;
import com.reedelk.plugin.editor.properties.commons.PropertiesPanelHolder;
import com.reedelk.plugin.editor.properties.renderer.PropertyTypeRenderer;
import com.reedelk.plugin.editor.properties.renderer.PropertyTypeRendererFactory;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import static java.awt.BorderLayout.CENTER;

public class MapTableCustomObjectPanel extends DisposablePanel {

    public MapTableCustomObjectPanel(Module module, ComponentDataHolder dataHolder, TypeObjectDescriptor objectDescriptor) {
        List<PropertyDescriptor> descriptors = objectDescriptor.getObjectProperties();

        CustomMapObjectPanelHolder propertiesPanel =
                new CustomMapObjectPanelHolder(objectDescriptor.getTypeFullyQualifiedName(), dataHolder, descriptors);

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

    static class CustomMapObjectPanelHolder extends PropertiesPanelHolder {

        CustomMapObjectPanelHolder(@NotNull String componentFullyQualifiedName,
                                   @NotNull ComponentDataHolder dataHolder,
                                   @NotNull List<PropertyDescriptor> descriptors) {
            super(componentFullyQualifiedName, dataHolder, descriptors);
        }

        /**
         * We override the default Properties panel holder because the accessors of data
         * displayed in this panel goes to the configuration file and not in the graph
         * component's data (snapshot).
         */
        @Override
        protected PropertyAccessor getAccessor(String propertyName, TypeDescriptor propertyType, ComponentDataHolder dataHolder) {
            return PropertyAccessorFactory.get()
                    .typeDescriptor(propertyType)
                    .propertyName(propertyName)
                    .dataHolder(dataHolder)
                    .build();
        }
    }
}
