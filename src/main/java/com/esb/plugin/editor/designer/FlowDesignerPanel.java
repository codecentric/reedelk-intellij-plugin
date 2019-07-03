package com.esb.plugin.editor.designer;

import com.esb.plugin.commons.Half;
import com.esb.plugin.editor.designer.widget.FlowMetadata;
import com.esb.plugin.editor.designer.widget.InboundLane;
import com.esb.plugin.graph.FlowSnapshot;

import java.awt.*;

public class FlowDesignerPanel extends DesignerPanel {

    private InboundLane inboundLane;
    private FlowMetadata flowMetadata;

    public FlowDesignerPanel(FlowSnapshot snapshot, DesignerPanelActionHandler actionHandler) {
        super(snapshot, actionHandler);
        this.inboundLane = new InboundLane(snapshot, TOP_PADDING);
        this.flowMetadata = new FlowMetadata(snapshot, Half.of(TOP_PADDING), AbstractGraphNode.NODE_WIDTH);
    }

    @Override
    protected void onPrePaint(Graphics2D graphics) {
        inboundLane.draw(snapshot.getGraph(), graphics, this);
        flowMetadata.draw(graphics);
    }
}
