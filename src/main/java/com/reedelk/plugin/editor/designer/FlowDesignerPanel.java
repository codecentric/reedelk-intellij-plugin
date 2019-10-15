package com.reedelk.plugin.editor.designer;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.commons.Half;
import com.reedelk.plugin.editor.designer.widget.FlowMetadata;
import com.reedelk.plugin.editor.designer.widget.InboundLane;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.FlowSnapshot;
import com.reedelk.plugin.service.project.SelectableItem;
import com.reedelk.plugin.service.project.SelectableItemFlow;

import java.awt.*;

public class FlowDesignerPanel extends DesignerPanel {

    private InboundLane inboundLane;
    private FlowMetadata flowMetadata;
    private SelectableItem nothingSelectedItem;

    public FlowDesignerPanel(Module module, FlowSnapshot snapshot, DesignerPanelActionHandler actionHandler) {
        super(module, snapshot, actionHandler);
        this.inboundLane = new InboundLane(TOP_PADDING, this);
        this.flowMetadata = new FlowMetadata(snapshot, Half.of(TOP_PADDING), AbstractGraphNode.NODE_WIDTH);
        this.nothingSelectedItem = new SelectableItemFlow(snapshot);
    }

    @Override
    protected void beforePaint(Graphics2D graphics) {
        FlowGraph graph = snapshot.getGraphOrThrowIfAbsent();
        inboundLane.draw(graph, graphics, FlowDesignerPanel.this);
        flowMetadata.draw(graphics);
    }

    @Override
    protected SelectableItem defaultSelectedItem() {
        return nothingSelectedItem;
    }
}