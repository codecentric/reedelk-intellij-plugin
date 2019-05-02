package com.esb.plugin.component.generic;

import com.esb.plugin.component.Component;
import com.esb.plugin.designer.properties.AbstractPropertyRenderer;
import com.esb.plugin.designer.properties.PropertyTracker;
import com.esb.plugin.designer.properties.widget.PropertyBox;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBPanel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class GenericComponentPropertyRenderer extends AbstractPropertyRenderer {

    public GenericComponentPropertyRenderer(Module module, FlowGraph graph, VirtualFile file) {
        super(module, graph, file);
    }

    @Override
    public JBPanel render(GraphNode node) {
        Component component = node.component();

        JBPanel propertiesBoxContainer = new JBPanel();
        propertiesBoxContainer.setLayout(new BoxLayout(propertiesBoxContainer, BoxLayout.PAGE_AXIS));

        component.getPropertiesNames().forEach(propertyName -> {
            PropertyBox propertyBox = createPropertyBox(component, propertyName);
            propertiesBoxContainer.add(propertyBox);
        });

        propertiesBoxContainer.add(Box.createVerticalGlue());
        return propertiesBoxContainer;
    }

    @NotNull
    private PropertyBox createPropertyBox(Component component, String propertyName) {
        PropertyTracker tracker = new PropertyTracker(component, propertyName);

        PropertyBox propertyBox = new PropertyBox(propertyName);
        propertyBox.setText(tracker.getValueAsString());
        propertyBox.addListener(newText -> tracker.onPropertyChange(module, graph, file, newText));
        return propertyBox;
    }

}
