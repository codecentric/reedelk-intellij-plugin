package de.codecentric.reedelk.plugin.editor.designer;

import de.codecentric.reedelk.plugin.editor.designer.dnd.DesignerPanelActionHandler;
import de.codecentric.reedelk.plugin.graph.FlowGraph;
import de.codecentric.reedelk.plugin.graph.FlowSnapshot;
import com.intellij.openapi.module.Module;
import de.codecentric.reedelk.plugin.commons.Half;
import de.codecentric.reedelk.plugin.editor.designer.text.FlowMetadata;
import de.codecentric.reedelk.plugin.editor.properties.selection.SelectableItemSubflow;

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
