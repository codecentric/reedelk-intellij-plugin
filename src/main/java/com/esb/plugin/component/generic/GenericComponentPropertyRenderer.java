package com.esb.plugin.component.generic;

import com.esb.plugin.component.ComponentData;
import com.esb.plugin.designer.properties.AbstractPropertyRenderer;
import com.esb.plugin.designer.properties.widget.DefaultPropertiesPanel;
import com.esb.plugin.graph.GraphSnapshot;
import com.esb.plugin.graph.node.GraphNode;
import com.intellij.ui.components.JBPanel;

public class GenericComponentPropertyRenderer extends AbstractPropertyRenderer {

    public GenericComponentPropertyRenderer(GraphSnapshot snapshot) {
        super(snapshot);
    }

    @Override
    public JBPanel render(GraphNode node) {
        ComponentData componentData = node.componentData();

        JBPanel propertiesListPanel = new DefaultPropertiesPanel();

        addPropertyField(componentData, "Description", propertiesListPanel);

        componentData.descriptorProperties().forEach(propertyName ->
                addPropertyField(componentData, propertyName, propertiesListPanel));

        return propertiesListPanel;
    }

}
