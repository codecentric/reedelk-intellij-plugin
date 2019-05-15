package com.esb.plugin.component.fork;

import com.esb.plugin.component.ComponentData;
import com.esb.plugin.component.ComponentPropertyDescriptor;
import com.esb.plugin.designer.properties.renderer.AbstractPropertiesRenderer;
import com.esb.plugin.designer.properties.renderer.PropertyRendererFactory;
import com.esb.plugin.designer.properties.widget.DefaultPropertiesPanel;
import com.esb.plugin.graph.GraphSnapshot;
import com.esb.plugin.graph.node.GraphNode;
import com.intellij.ui.components.JBPanel;

import java.util.List;

public class ForkPropertiesRenderer extends AbstractPropertiesRenderer {

    public ForkPropertiesRenderer(GraphSnapshot snapshot) {
        super(snapshot);
    }

    @Override
    public JBPanel render(GraphNode node) {
        ComponentData componentData = node.componentData();

        DefaultPropertiesPanel panel = new DefaultPropertiesPanel();

        List<ComponentPropertyDescriptor> componentProperties = componentData.getComponentPropertyDescriptors();
        componentProperties.forEach(propertyDescriptor ->
                PropertyRendererFactory.get()
                        .from(propertyDescriptor.getPropertyType())
                        .render(propertyDescriptor, componentData, panel, snapshot));

        return panel;
    }

}
