package com.esb.plugin.component.generic;

import com.esb.plugin.component.ComponentData;
import com.esb.plugin.designer.properties.AbstractPropertyRenderer;
import com.esb.plugin.designer.properties.widget.PropertyBox;
import com.esb.plugin.graph.GraphSnapshot;
import com.esb.plugin.graph.node.GraphNode;
import com.intellij.ui.components.JBPanel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class GenericComponentPropertyRenderer extends AbstractPropertyRenderer {

    public GenericComponentPropertyRenderer(GraphSnapshot snapshot) {
        super(snapshot);
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
        PropertyBox propertyBox = new PropertyBox(propertyName);
        propertyBox.setText((String) componentData.get(propertyName.toLowerCase()));
        propertyBox.addListener(newText -> {
            componentData.set(propertyName.toLowerCase(), newText);
            snapshot.onDataChange();
        });
        return propertyBox;
    }

}
