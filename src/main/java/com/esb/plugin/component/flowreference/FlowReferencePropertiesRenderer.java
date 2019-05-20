package com.esb.plugin.component.flowreference;

import com.esb.plugin.component.ComponentData;
import com.esb.plugin.component.ComponentPropertyDescriptor;
import com.esb.plugin.component.generic.GenericComponentPropertiesRenderer;
import com.esb.plugin.graph.GraphSnapshot;
import com.esb.plugin.graph.node.GraphNode;
import com.intellij.ui.components.JBPanel;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

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

        List<ComponentPropertyDescriptor> descriptors = new ArrayList<>();
        componentData.getDataProperties()
                .stream()
                .filter(propertyName -> !propertyName.equals(FlowReference.ref()))
                .forEach(property ->
                        componentData.getPropertyDescriptor(property)
                                .ifPresent(descriptors::add));


        JBPanel genericPropertiesPanel = createPropertiesPanelFrom(descriptors, componentData);

        JBPanel selectSubFlowDropDown = new JBPanel();

        JBPanel container = new JBPanel();
        container.setLayout(new BorderLayout());
        container.add(genericPropertiesPanel, NORTH);
        container.add(selectSubFlowDropDown, CENTER);
        return container;
    }
}
