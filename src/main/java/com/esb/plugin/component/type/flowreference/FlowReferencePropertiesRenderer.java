package com.esb.plugin.component.type.flowreference;

import com.esb.plugin.component.domain.ComponentData;
import com.esb.plugin.component.domain.ComponentPropertyDescriptor;
import com.esb.plugin.component.type.generic.GenericComponentPropertiesRenderer;
import com.esb.plugin.graph.FlowSnapshot;
import com.esb.plugin.graph.node.GraphNode;
import com.intellij.ui.components.JBPanel;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static com.esb.internal.commons.JsonParser.FlowReference;
import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.NORTH;

public class FlowReferencePropertiesRenderer extends GenericComponentPropertiesRenderer {

    public FlowReferencePropertiesRenderer(FlowSnapshot snapshot) {
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
