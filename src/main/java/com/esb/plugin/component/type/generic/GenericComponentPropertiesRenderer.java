package com.esb.plugin.component.type.generic;

import com.esb.plugin.component.domain.ComponentData;
import com.esb.plugin.component.domain.ComponentDataHolder;
import com.esb.plugin.component.domain.ComponentPropertyDescriptor;
import com.esb.plugin.component.domain.TypeDescriptor;
import com.esb.plugin.editor.properties.accessor.PropertyAccessor;
import com.esb.plugin.editor.properties.accessor.PropertyAccessorFactory;
import com.esb.plugin.editor.properties.renderer.AbstractNodePropertiesRenderer;
import com.esb.plugin.editor.properties.renderer.TypePropertyRenderer;
import com.esb.plugin.editor.properties.renderer.TypeRendererFactory;
import com.esb.plugin.editor.properties.widget.DefaultPropertiesPanel;
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

            String displayName = descriptor.getDisplayName();

            String propertyName = descriptor.getPropertyName();

            TypeDescriptor propertyType = descriptor.getPropertyType();

            PropertyAccessor setter = getAccessor(propertyName, propertyType, data);

            TypePropertyRenderer renderer = TypeRendererFactory.get().from(propertyType);

            JComponent renderedComponent = renderer.render(module, descriptor, setter);

            renderer.addToParent(propertiesPanel, renderedComponent, displayName);

        });

        return propertiesPanel;
    }

    private PropertyAccessor getAccessor(String propertyName, TypeDescriptor propertyType, ComponentDataHolder dataHolder) {
        return PropertyAccessorFactory.get()
                .typeDescriptor(propertyType)
                .propertyName(propertyName)
                .dataHolder(dataHolder)
                .snapshot(snapshot)
                .build();
    }
}
