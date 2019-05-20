package com.esb.plugin.component.generic;

import com.esb.plugin.component.ComponentData;
import com.esb.plugin.component.ComponentPropertyDescriptor;
import com.esb.plugin.component.TypeDescriptor;
import com.esb.plugin.designer.properties.renderer.node.AbstractNodePropertiesRenderer;
import com.esb.plugin.designer.properties.renderer.type.TypeRendererFactory;
import com.esb.plugin.designer.properties.widget.DefaultPropertiesPanel;
import com.esb.plugin.designer.properties.widget.FormBuilder;
import com.esb.plugin.graph.GraphSnapshot;
import com.esb.plugin.graph.node.GraphNode;
import com.intellij.ui.components.JBPanel;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class GenericComponentPropertiesRenderer extends AbstractNodePropertiesRenderer {

    public GenericComponentPropertiesRenderer(GraphSnapshot snapshot) {
        super(snapshot);
    }

    @Override
    public JBPanel render(GraphNode node) {
        ComponentData componentData = node.componentData();
        List<ComponentPropertyDescriptor> descriptors = new ArrayList<>();
        componentData.getDataProperties().forEach(property ->
                componentData.getPropertyDescriptor(property)
                        .ifPresent(descriptors::add));
        return createPropertiesPanelFrom(descriptors, componentData);
    }

    protected JBPanel createPropertiesPanelFrom(List<ComponentPropertyDescriptor> propertyDescriptors, ComponentData data) {
        DefaultPropertiesPanel propertiesPanel = new DefaultPropertiesPanel();
        propertyDescriptors.forEach(descriptor -> {
            final String displayName = descriptor.getDisplayName();
            final TypeDescriptor propertyType = descriptor.getPropertyType();
            final JComponent renderedComponent =
                    TypeRendererFactory.get().from(propertyType)
                            .render(descriptor, data, snapshot);
            FormBuilder.get()
                    .addLabel(displayName, propertiesPanel)
                    .addLastField(renderedComponent, propertiesPanel);
        });
        return propertiesPanel;
    }
}
