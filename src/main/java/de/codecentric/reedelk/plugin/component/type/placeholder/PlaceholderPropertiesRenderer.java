package de.codecentric.reedelk.plugin.component.type.placeholder;

import com.intellij.openapi.module.Module;
import de.codecentric.reedelk.plugin.editor.properties.commons.DisposablePanel;
import de.codecentric.reedelk.plugin.editor.properties.renderer.AbstractComponentPropertiesRenderer;
import de.codecentric.reedelk.plugin.graph.FlowSnapshot;
import de.codecentric.reedelk.plugin.graph.node.GraphNode;

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
