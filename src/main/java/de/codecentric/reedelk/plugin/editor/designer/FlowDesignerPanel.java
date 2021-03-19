package de.codecentric.reedelk.plugin.editor.designer;

import de.codecentric.reedelk.plugin.editor.designer.dnd.DesignerPanelActionHandler;
import de.codecentric.reedelk.plugin.graph.FlowGraph;
import de.codecentric.reedelk.plugin.graph.FlowSnapshot;
import com.intellij.openapi.module.Module;
import de.codecentric.reedelk.plugin.commons.Half;
import de.codecentric.reedelk.plugin.editor.designer.misc.InboundLane;
import de.codecentric.reedelk.plugin.editor.designer.text.FlowMetadata;
import de.codecentric.reedelk.plugin.editor.properties.selection.SelectableItemFlow;

import java.awt.*;
import java.awt.image.ImageObserver;

public class FlowDesignerPanel extends DesignerPanel {

    private final transient InboundLane inboundLane;
    private final transient FlowMetadata flowMetadata;

    public FlowDesignerPanel(Module module, FlowSnapshot snapshot, DesignerPanelActionHandler actionHandler) {
        super(module, snapshot, actionHandler, new SelectableItemFlow(snapshot));
        this.inboundLane = new InboundLane(TOP_PADDING, this);
        this.flowMetadata = new FlowMetadata(snapshot, Half.of(TOP_PADDING), AbstractGraphNode.NODE_WIDTH);
    }

    @Override
    protected void beforePaint(FlowGraph graph, Graphics2D graphics, ImageObserver imageObserver) {
        inboundLane.draw(graph, graphics, FlowDesignerPanel.this);
        flowMetadata.draw(graphics);
    }
}
