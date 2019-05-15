package com.esb.plugin.component.generic;

import com.esb.plugin.component.ComponentData;
import com.esb.plugin.designer.properties.AbstractPropertiesRenderer;
import com.esb.plugin.designer.properties.PrimitiveTypePropertyRenderer;
import com.esb.plugin.designer.properties.widget.DefaultPropertiesPanel;
import com.esb.plugin.graph.GraphSnapshot;
import com.esb.plugin.graph.node.GraphNode;
import com.intellij.ui.components.JBPanel;

public class GenericComponentPropertiesRenderer extends AbstractPropertiesRenderer {

    public GenericComponentPropertiesRenderer(GraphSnapshot snapshot) {
        super(snapshot);
    }

    @Override
    public JBPanel render(GraphNode node) {
        ComponentData componentData = node.componentData();
        DefaultPropertiesPanel panel = new DefaultPropertiesPanel();

        componentData.getComponentPropertyDescriptors()
                .forEach(propertyDescriptor -> {
                    PrimitiveTypePropertyRenderer renderer = new PrimitiveTypePropertyRenderer();
                    renderer.render(propertyDescriptor, componentData, panel, snapshot);
                });

        return panel;
    }

}
