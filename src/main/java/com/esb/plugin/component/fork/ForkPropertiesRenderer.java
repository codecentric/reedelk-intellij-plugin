package com.esb.plugin.component.fork;

import com.esb.plugin.component.ComponentData;
import com.esb.plugin.designer.properties.AbstractPropertiesRenderer;
import com.esb.plugin.designer.properties.widget.DefaultPropertiesPanel;
import com.esb.plugin.graph.GraphSnapshot;
import com.esb.plugin.graph.node.GraphNode;
import com.intellij.ui.components.JBPanel;

import static com.esb.internal.commons.JsonParser.Fork;

public class ForkPropertiesRenderer extends AbstractPropertiesRenderer {

    public ForkPropertiesRenderer(GraphSnapshot snapshot) {
        super(snapshot);
    }

    @Override
    public JBPanel render(GraphNode node) {
        ComponentData componentData = node.componentData();

        DefaultPropertiesPanel propertiesListPanel = new DefaultPropertiesPanel(snapshot, componentData);

        // TODO: Maybe we should use the component descriptor instead of JsonParser!?
        // TODO: This should come from the component definition
        propertiesListPanel.addPropertyField("Thread Pool Size", Fork.threadPoolSize());

        return propertiesListPanel;
    }

}
