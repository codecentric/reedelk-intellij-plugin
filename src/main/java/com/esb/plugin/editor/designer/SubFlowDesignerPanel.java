package com.esb.plugin.editor.designer;

import com.esb.plugin.commons.Half;
import com.esb.plugin.editor.designer.widget.FlowMetadata;
import com.esb.plugin.graph.FlowSnapshot;

import java.awt.*;

public class SubFlowDesignerPanel extends DesignerPanel {

    private FlowMetadata flowMetadata;

    public SubFlowDesignerPanel(FlowSnapshot snapshot, DesignerPanelActionHandler actionHandler) {
        super(snapshot, actionHandler);
        this.flowMetadata = new FlowMetadata(snapshot, Half.of(TOP_PADDING));
    }

    @Override
    protected void onPrePaint(Graphics2D graphics) {
        flowMetadata.draw(graphics);
    }
}
