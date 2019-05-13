package com.esb.plugin.component.fork;

import com.esb.plugin.component.ComponentData;
import com.esb.plugin.designer.properties.AbstractPropertyRenderer;
import com.esb.plugin.designer.properties.widget.DefaultPropertiesPanel;
import com.esb.plugin.graph.GraphSnapshot;
import com.esb.plugin.graph.node.GraphNode;
import com.intellij.ui.components.JBPanel;

public class ForkPropertyRenderer extends AbstractPropertyRenderer {

    public ForkPropertyRenderer(GraphSnapshot snapshot) {
        super(snapshot);
    }

    @Override
    public JBPanel render(GraphNode node) {
        ComponentData componentData = node.componentData();

        JBPanel propertiesListPanel = new DefaultPropertiesPanel();
        addPropertyField(componentData, "Description", propertiesListPanel);
        addPropertyField(componentData, "Thread Pool Size", propertiesListPanel);
        return propertiesListPanel;
    }

}
