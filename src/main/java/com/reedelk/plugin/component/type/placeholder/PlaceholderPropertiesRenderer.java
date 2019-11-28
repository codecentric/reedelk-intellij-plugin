package com.reedelk.plugin.component.type.placeholder;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.editor.properties.renderer.AbstractComponentPropertiesRenderer;
import com.reedelk.plugin.editor.properties.widget.DisposablePanel;
import com.reedelk.plugin.graph.FlowSnapshot;
import com.reedelk.plugin.graph.node.GraphNode;

public class PlaceholderPropertiesRenderer extends AbstractComponentPropertiesRenderer {

    public PlaceholderPropertiesRenderer(FlowSnapshot snapshot, Module module) {
        super(snapshot, module);
    }

    @Override
    public DisposablePanel render(GraphNode node) {
        // There are no properties to render for this type of node.
        return new DisposablePanel();
    }
}
