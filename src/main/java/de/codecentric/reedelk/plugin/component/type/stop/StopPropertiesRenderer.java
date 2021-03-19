package de.codecentric.reedelk.plugin.component.type.stop;

import de.codecentric.reedelk.plugin.editor.properties.commons.DisposablePanel;
import de.codecentric.reedelk.plugin.editor.properties.renderer.AbstractComponentPropertiesRenderer;
import de.codecentric.reedelk.plugin.graph.FlowSnapshot;
import de.codecentric.reedelk.plugin.graph.node.GraphNode;
import com.intellij.openapi.module.Module;

public class StopPropertiesRenderer extends AbstractComponentPropertiesRenderer {

    public StopPropertiesRenderer(FlowSnapshot snapshot, Module module) {
        super(snapshot, module);
    }

    @Override
    public DisposablePanel render(GraphNode node) {
        throw new UnsupportedOperationException("Stop node cannot be rendered");
    }
}
