package com.reedelk.plugin.component.type.stop;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.editor.properties.renderer.AbstractComponentPropertiesRenderer;
import com.reedelk.plugin.editor.properties.widget.DisposablePanel;
import com.reedelk.plugin.graph.FlowSnapshot;
import com.reedelk.plugin.graph.node.GraphNode;

public class StopPropertiesRenderer extends AbstractComponentPropertiesRenderer {

    public StopPropertiesRenderer(FlowSnapshot snapshot, Module module) {
        super(snapshot, module);
    }

    @Override
    public DisposablePanel render(GraphNode node) {
        throw new UnsupportedOperationException("Stop node cannot be rendered");
    }
}
