package com.esb.plugin.component.flowreference;

import com.esb.plugin.component.ComponentData;
import com.esb.plugin.component.ComponentPropertyDescriptor;
import com.esb.plugin.component.generic.GenericComponentPropertiesRenderer;
import com.esb.plugin.graph.GraphSnapshot;
import com.esb.plugin.graph.node.GraphNode;
import com.intellij.ui.components.JBPanel;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

import static com.esb.internal.commons.JsonParser.FlowReference;
import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.NORTH;

public class FlowReferencePropertiesRenderer extends GenericComponentPropertiesRenderer {

    public FlowReferencePropertiesRenderer(GraphSnapshot snapshot) {
        super(snapshot);
    }

    @Override
    public JBPanel render(GraphNode node) {
        ComponentData componentData = node.componentData();
        List<ComponentPropertyDescriptor> componentPropertyDescriptors = componentData.getComponentPropertyDescriptors();
        List<ComponentPropertyDescriptor> genericProperties = componentPropertyDescriptors
                .stream()
                .filter(descriptor -> !descriptor.getPropertyName().equals(FlowReference.ref())).collect(Collectors.toList());
        JBPanel genericPropertiesPanel = createPropertiesPanelFrom(genericProperties, componentData);

        JBPanel selectSubFlowDropDown = new JBPanel();

        JBPanel container = new JBPanel();
        container.setLayout(new BorderLayout());
        container.add(genericPropertiesPanel, NORTH);
        container.add(selectSubFlowDropDown, CENTER);
        return container;
    }
}
