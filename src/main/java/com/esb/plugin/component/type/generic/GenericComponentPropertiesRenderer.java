package com.esb.plugin.component.type.generic;

import com.esb.plugin.component.domain.ComponentData;
import com.esb.plugin.component.domain.ComponentPropertyDescriptor;
import com.esb.plugin.component.domain.TypeDescriptor;
import com.esb.plugin.editor.properties.renderer.node.AbstractNodePropertiesRenderer;
import com.esb.plugin.editor.properties.renderer.type.TypeRendererFactory;
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
