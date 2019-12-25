package com.reedelk.plugin.editor.designer;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.commons.Half;
import com.reedelk.plugin.editor.designer.dnd.DesignerPanelActionHandler;
import com.reedelk.plugin.editor.designer.misc.InboundLane;
import com.reedelk.plugin.editor.designer.text.FlowMetadata;
import com.reedelk.plugin.editor.properties.selection.SelectableItemFlow;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.FlowSnapshot;

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
