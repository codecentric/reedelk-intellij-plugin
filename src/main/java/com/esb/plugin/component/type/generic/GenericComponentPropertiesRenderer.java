package com.esb.plugin.component.type.generic;

import com.esb.plugin.component.domain.ComponentData;
import com.esb.plugin.component.domain.ComponentPropertyDescriptor;
import com.esb.plugin.component.domain.TypeDescriptor;
import com.esb.plugin.editor.properties.accessor.PropertyAccessor;
import com.esb.plugin.editor.properties.renderer.AbstractNodePropertiesRenderer;
import com.esb.plugin.editor.properties.renderer.TypePropertyRenderer;
import com.esb.plugin.editor.properties.renderer.TypeRendererFactory;
import com.esb.plugin.editor.properties.widget.DisposablePanel;
import com.esb.plugin.editor.properties.widget.PropertiesPanelHolder;
import com.esb.plugin.graph.FlowSnapshot;
import com.esb.plugin.graph.node.GraphNode;
import com.intellij.openapi.module.Module;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.List;

public class GenericComponentPropertiesRenderer extends AbstractNodePropertiesRenderer {

    public GenericComponentPropertiesRenderer(FlowSnapshot snapshot, Module module) {
        super(snapshot, module);
    }

    @Override
    public DisposablePanel render(GraphNode node) {

        ComponentData componentData = node.componentData();

        List<ComponentPropertyDescriptor> descriptors = componentData.getPropertiesDescriptors();

        return getDefaultPropertiesPanel(componentData, descriptors);
    }

    @NotNull
    protected PropertiesPanelHolder getDefaultPropertiesPanel(ComponentData componentData, List<ComponentPropertyDescriptor> descriptors) {
        PropertiesPanelHolder propertiesPanel = new PropertiesPanelHolder(componentData, descriptors, snapshot);

        descriptors.forEach(propertyDescriptor -> {

            String displayName = propertyDescriptor.getDisplayName();

            String propertyName = propertyDescriptor.getPropertyName();

            PropertyAccessor propertyAccessor = propertiesPanel.getAccessor(propertyName);

            TypeDescriptor propertyType = propertyDescriptor.getPropertyType();

            TypePropertyRenderer renderer = TypeRendererFactory.get().from(propertyType);

            JComponent renderedComponent =
                    renderer.render(module, propertyDescriptor, propertyAccessor, propertiesPanel);

            renderer.addToParent(propertiesPanel, renderedComponent, displayName);

        });

        return propertiesPanel;
    }
}
