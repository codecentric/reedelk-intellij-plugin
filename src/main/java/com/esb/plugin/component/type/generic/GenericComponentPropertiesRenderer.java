package com.esb.plugin.component.type.generic;

import com.esb.plugin.component.domain.*;
import com.esb.plugin.editor.properties.accessor.PropertyAccessor;
import com.esb.plugin.editor.properties.accessor.PropertyAccessorFactory;
import com.esb.plugin.editor.properties.renderer.node.AbstractNodePropertiesRenderer;
import com.esb.plugin.editor.properties.renderer.type.TypeRendererFactory;
import com.esb.plugin.editor.properties.widget.ContainerFactory;
import com.esb.plugin.editor.properties.widget.DefaultPropertiesPanel;
import com.esb.plugin.editor.properties.widget.FormBuilder;
import com.esb.plugin.graph.FlowSnapshot;
import com.esb.plugin.graph.node.GraphNode;
import com.intellij.openapi.module.Module;
import com.intellij.ui.components.JBPanel;

import javax.swing.*;
import java.util.List;

public class GenericComponentPropertiesRenderer extends AbstractNodePropertiesRenderer {

    public GenericComponentPropertiesRenderer(FlowSnapshot snapshot, Module module) {
        super(snapshot, module);
    }

    @Override
    public JBPanel render(GraphNode node) {
        ComponentData componentData = node.componentData();
        List<ComponentPropertyDescriptor> descriptors = componentData.getPropertiesDescriptors();
        return createPropertiesPanelFrom(descriptors, componentData);
    }

    protected JBPanel createPropertiesPanelFrom(List<ComponentPropertyDescriptor> propertyDescriptors, ComponentData data) {
        DefaultPropertiesPanel propertiesPanel = new DefaultPropertiesPanel();
        propertyDescriptors.forEach(descriptor -> {

            final String displayName = descriptor.getDisplayName();
            final String propertyName = descriptor.getPropertyName();
            final TypeDescriptor propertyType = descriptor.getPropertyType();

            final PropertyAccessor setter = getAccessor(propertyName, propertyType, data);
            final JComponent renderedComponent =
                    TypeRendererFactory.get()
                            .from(propertyType)
                            .render(descriptor, setter, snapshot);

            // If the property type is a complex object, we wrap it in a
            // bordered box with title the name of the object property.
            if (propertyType instanceof TypeObjectDescriptor) {
                JBPanel wrappedRenderedComponent = ContainerFactory
                        .createObjectTypeContainer(descriptor.getDisplayName(), renderedComponent);
                FormBuilder.get()
                        .addLastField(wrappedRenderedComponent, propertiesPanel);
            } else {
                FormBuilder.get()
                        .addLabel(displayName, propertiesPanel)
                        .addLastField(renderedComponent, propertiesPanel);
            }
        });
        return propertiesPanel;
    }

    private PropertyAccessor getAccessor(String propertyName, TypeDescriptor propertyType, ComponentDataHolder dataHolder) {
        return PropertyAccessorFactory.get()
                .propertyName(propertyName)
                .typeDescriptor(propertyType)
                .dataHolder(dataHolder)
                .build();
    }
}
