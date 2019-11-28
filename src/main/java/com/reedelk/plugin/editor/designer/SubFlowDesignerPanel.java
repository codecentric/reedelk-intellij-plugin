package com.reedelk.plugin.editor.designer;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.commons.Half;
import com.reedelk.plugin.editor.designer.dnd.DesignerPanelActionHandler;
import com.reedelk.plugin.editor.designer.text.FlowMetadata;
import com.reedelk.plugin.graph.FlowSnapshot;
import com.reedelk.plugin.service.project.SelectableItem;
import com.reedelk.plugin.service.project.SelectableItemSubflow;

import java.awt.*;

public class SubFlowDesignerPanel extends DesignerPanel {

    private transient final FlowMetadata flowMetadata;
    private transient final SelectableItem nothingSelectedItem;

    public SubFlowDesignerPanel(Module module, FlowSnapshot snapshot, DesignerPanelActionHandler actionHandler) {
        super(module, snapshot, actionHandler);
        this.flowMetadata = new FlowMetadata(snapshot, Half.of(TOP_PADDING));
        this.nothingSelectedItem = new SelectableItemSubflow(snapshot);
    }

    @Override
    protected void beforePaint(Graphics2D graphics) {
        flowMetadata.draw(graphics);
    }

    @Override
    protected SelectableItem defaultSelectedItem() {
        return nothingSelectedItem;
    }
}
