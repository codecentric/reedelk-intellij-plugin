package com.esb.plugin.component.generic;

import com.esb.plugin.component.ComponentData;
import com.esb.plugin.designer.properties.AbstractPropertyRenderer;
import com.esb.plugin.designer.properties.DefaultPropertyTracker;
import com.esb.plugin.designer.properties.PropertyTracker;
import com.esb.plugin.designer.properties.widget.PropertyBox;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.manager.GraphChangeListener;
import com.esb.plugin.graph.node.GraphNode;
import com.intellij.ui.components.JBPanel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class GenericComponentPropertyRenderer extends AbstractPropertyRenderer {

    public GenericComponentPropertyRenderer(FlowGraph graph, GraphChangeListener listener) {
        super(graph, listener);
    }

    @Override
    public JBPanel render(GraphNode node) {
        ComponentData componentData = node.component();

        JBPanel propertiesBoxContainer = new JBPanel();
        propertiesBoxContainer.setLayout(new BoxLayout(propertiesBoxContainer, BoxLayout.PAGE_AXIS));

        componentData.descriptorProperties().forEach(propertyName -> {
            PropertyBox propertyBox = createPropertyBox(componentData, propertyName);
            propertiesBoxContainer.add(propertyBox);
        });

        propertiesBoxContainer.add(Box.createVerticalGlue());
        return propertiesBoxContainer;
    }

    @NotNull
    private PropertyBox createPropertyBox(ComponentData componentData, String propertyName) {
        PropertyTracker tracker = new DefaultPropertyTracker(componentData, propertyName, graph, listener);

        PropertyBox propertyBox = new PropertyBox(propertyName);
        propertyBox.setText(tracker.getValueAsString());
        propertyBox.addListener(newText -> tracker.onPropertyChange(newText));
        return propertyBox;
    }

}
