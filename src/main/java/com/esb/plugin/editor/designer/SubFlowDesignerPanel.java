package com.esb.plugin.editor.designer;

import com.esb.plugin.commons.Half;
import com.esb.plugin.editor.designer.widget.FlowMetadata;
import com.esb.plugin.graph.FlowSnapshot;
import com.intellij.openapi.module.Module;

import java.awt.*;

public class SubFlowDesignerPanel extends DesignerPanel {

    private FlowMetadata flowMetadata;

    public SubFlowDesignerPanel(Module module, FlowSnapshot snapshot, DesignerPanelActionHandler actionHandler) {
        super(module, snapshot, actionHandler);
        this.flowMetadata = new FlowMetadata(snapshot, Half.of(TOP_PADDING));
    }

    @Override
    protected void onPrePaint(Graphics2D graphics) {
        flowMetadata.draw(graphics);
    }
}
