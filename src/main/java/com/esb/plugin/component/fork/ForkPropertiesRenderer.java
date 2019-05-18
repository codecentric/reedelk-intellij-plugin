package com.esb.plugin.component.fork;

import com.esb.plugin.component.ComponentData;
import com.esb.plugin.component.ComponentPropertyDescriptor;
import com.esb.plugin.component.generic.GenericComponentPropertiesRenderer;
import com.esb.plugin.graph.GraphSnapshot;
import com.esb.plugin.graph.node.GraphNode;
import com.intellij.ui.components.JBPanel;

import java.util.List;

public class ForkPropertiesRenderer extends GenericComponentPropertiesRenderer {

    public ForkPropertiesRenderer(GraphSnapshot snapshot) {
        super(snapshot);
    }

    @Override
    public JBPanel render(GraphNode node) {
        ComponentData componentData = node.componentData();
        List<ComponentPropertyDescriptor> componentPropertyDescriptors = componentData.getComponentPropertyDescriptors();
        return createPropertiesPanelFrom(componentPropertyDescriptors, componentData);
    }
}
