package com.reedelk.plugin.component.type.generic;

import com.intellij.openapi.module.Module;
import com.reedelk.component.descriptor.ComponentData;
import com.reedelk.component.descriptor.ComponentPropertyDescriptor;
import com.reedelk.component.descriptor.TypeDescriptor;
import com.reedelk.plugin.editor.properties.accessor.PropertyAccessor;
import com.reedelk.plugin.editor.properties.commons.DisposablePanel;
import com.reedelk.plugin.editor.properties.commons.PropertiesPanelHolder;
import com.reedelk.plugin.editor.properties.renderer.AbstractComponentPropertiesRenderer;
import com.reedelk.plugin.editor.properties.renderer.PropertyTypeRenderer;
import com.reedelk.plugin.editor.properties.renderer.PropertyTypeRendererFactory;
import com.reedelk.plugin.graph.FlowSnapshot;
import com.reedelk.plugin.graph.node.GraphNode;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.List;

public class GenericComponentPropertiesRenderer extends AbstractComponentPropertiesRenderer {

    public GenericComponentPropertiesRenderer(FlowSnapshot snapshot, Module module) {
        super(snapshot, module);
    }

    @Override
    public DisposablePanel render(GraphNode node) {

        ComponentData componentData = node.componentData();

        List<ComponentPropertyDescriptor> descriptors = componentData.getPropertiesDescriptors();

        return getDefaultPropertiesPanel(componentData.getFullyQualifiedName(), componentData, descriptors);
    }

    @NotNull
    protected PropertiesPanelHolder getDefaultPropertiesPanel(String componentFullyQualifiedName, ComponentData componentData, List<ComponentPropertyDescriptor> descriptors) {

        PropertiesPanelHolder propertiesPanel = new PropertiesPanelHolder(componentFullyQualifiedName, componentData, descriptors, snapshot);

        descriptors.forEach(descriptor -> {

            String propertyName = descriptor.getPropertyName();

            PropertyAccessor propertyAccessor = propertiesPanel.getAccessor(propertyName);

            TypeDescriptor propertyType = descriptor.getPropertyType();

            PropertyTypeRenderer renderer = PropertyTypeRendererFactory.get().from(propertyType);

            JComponent renderedComponent = renderer.render(module, descriptor, propertyAccessor, propertiesPanel);

            renderer.addToParent(propertiesPanel, renderedComponent, descriptor, propertiesPanel);

        });

        return propertiesPanel;
    }
}
