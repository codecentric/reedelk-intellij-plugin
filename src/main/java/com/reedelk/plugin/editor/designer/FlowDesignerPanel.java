package com.reedelk.plugin.editor.designer;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.commons.Half;
import com.reedelk.plugin.editor.designer.dnd.DesignerPanelActionHandler;
import com.reedelk.plugin.editor.designer.misc.InboundLane;
import com.reedelk.plugin.editor.designer.text.FlowMetadata;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.FlowSnapshot;
import com.reedelk.plugin.service.project.impl.designerselection.SelectableItem;
import com.reedelk.plugin.service.project.impl.designerselection.SelectableItemFlow;

import java.awt.*;

public class FlowDesignerPanel extends DesignerPanel {

    private final transient InboundLane inboundLane;
    private final transient FlowMetadata flowMetadata;
    private final transient SelectableItem nothingSelectedItem;

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
