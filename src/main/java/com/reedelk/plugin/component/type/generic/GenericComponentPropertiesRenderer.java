package com.reedelk.plugin.component.type.generic;

import com.intellij.openapi.module.Module;
import com.reedelk.module.descriptor.model.PropertyDescriptor;
import com.reedelk.plugin.component.ComponentData;
import com.reedelk.plugin.editor.properties.commons.DisposablePanel;
import com.reedelk.plugin.editor.properties.commons.PropertiesPanelHolder;
import com.reedelk.plugin.editor.properties.renderer.AbstractComponentPropertiesRenderer;
import com.reedelk.plugin.graph.FlowSnapshot;
import com.reedelk.plugin.graph.node.GraphNode;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GenericComponentPropertiesRenderer extends AbstractComponentPropertiesRenderer {

    public GenericComponentPropertiesRenderer(FlowSnapshot snapshot, Module module) {
        super(snapshot, module);
    }

    @Override
    public DisposablePanel render(GraphNode node) {

        ComponentData componentData = node.componentData();

        List<PropertyDescriptor> descriptors = componentData.getPropertiesDescriptors();

        return createDefaultPropertiesPanel(componentData.getFullyQualifiedName(), componentData, descriptors);
    }

    @NotNull
    protected PropertiesPanelHolder createDefaultPropertiesPanel(String componentFullyQualifiedName, ComponentData componentData, List<PropertyDescriptor> descriptors) {
        return new PropertiesPanelHolder(module, componentFullyQualifiedName, componentData, descriptors, snapshot);
    }
}
