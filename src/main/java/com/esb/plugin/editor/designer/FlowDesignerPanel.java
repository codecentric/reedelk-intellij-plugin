package com.esb.plugin.editor.designer;

import com.esb.plugin.commons.Half;
import com.esb.plugin.editor.designer.widget.FlowMetadata;
import com.esb.plugin.editor.designer.widget.InboundLane;
import com.esb.plugin.graph.FlowSnapshot;
import com.esb.plugin.service.project.SelectableItem;
import com.esb.plugin.service.project.SelectableItemFlow;
import com.intellij.openapi.module.Module;

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
    protected void onBeforePaint(Graphics2D graphics) {
        inboundLane.draw(snapshot.getGraph(), graphics, this);
        flowMetadata.draw(graphics);
    }

    @Override
    protected SelectableItem getNothingSelectedItem() {
        return nothingSelectedItem;
    }
}
