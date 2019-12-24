package com.reedelk.plugin.editor.designer;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.commons.Half;
import com.reedelk.plugin.editor.designer.dnd.DesignerPanelActionHandler;
import com.reedelk.plugin.editor.designer.text.FlowMetadata;
import com.reedelk.plugin.editor.properties.selection.SelectableItemSubflow;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.FlowSnapshot;

import java.awt.*;
import java.awt.image.ImageObserver;

public class SubFlowDesignerPanel extends DesignerPanel {

    private final transient FlowMetadata flowMetadata;

    public SubFlowDesignerPanel(Module module, FlowSnapshot snapshot, DesignerPanelActionHandler actionHandler) {
        super(module, snapshot, actionHandler, new SelectableItemSubflow(snapshot));
        this.flowMetadata = new FlowMetadata(snapshot, Half.of(TOP_PADDING));
    }

    @Override
    protected void beforePaint(FlowGraph graph, Graphics2D graphics, ImageObserver imageObserver) {
        flowMetadata.draw(graphics);
    }
}
