package com.esb.plugin.component.generic;

import com.esb.plugin.component.ComponentData;
import com.esb.plugin.component.ComponentPropertyDescriptor;
import com.esb.plugin.designer.properties.renderer.AbstractPropertiesRenderer;
import com.esb.plugin.designer.properties.renderer.PrimitiveTypePropertyRenderer;
import com.esb.plugin.designer.properties.widget.DefaultPropertiesPanel;
import com.esb.plugin.graph.GraphSnapshot;
import com.esb.plugin.graph.node.GraphNode;
import com.intellij.ui.components.JBPanel;

import java.util.List;

public class GenericComponentPropertiesRenderer extends AbstractPropertiesRenderer {

    public GenericComponentPropertiesRenderer(GraphSnapshot snapshot) {
        super(snapshot);
    }

    @Override
    public JBPanel render(GraphNode node) {
        ComponentData componentData = node.componentData();
        DefaultPropertiesPanel panel = new DefaultPropertiesPanel();

        List<ComponentPropertyDescriptor> componentProperties = componentData.getComponentPropertyDescriptors();
        componentProperties.forEach(propertyDescriptor -> {
            PrimitiveTypePropertyRenderer renderer = new PrimitiveTypePropertyRenderer();
            renderer.render(propertyDescriptor, componentData, panel, snapshot);
        });

        return panel;
    }

}
