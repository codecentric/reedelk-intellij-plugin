package com.esb.plugin.component.type.placeholder;

import com.esb.plugin.editor.properties.renderer.node.AbstractNodePropertiesRenderer;
import com.esb.plugin.graph.FlowSnapshot;
import com.esb.plugin.graph.node.GraphNode;
import com.intellij.openapi.module.Module;
import com.intellij.ui.components.JBPanel;

public class PlaceholderPropertiesRenderer extends AbstractNodePropertiesRenderer {

    public PlaceholderPropertiesRenderer(FlowSnapshot snapshot, Module module) {
        super(snapshot, module);
    }

    @Override
    public JBPanel render(GraphNode node) {
        // There are no properties to render for this type of node.
        return new JBPanel();
    }
}
