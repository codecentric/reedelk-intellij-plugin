package com.reedelk.plugin.editor.designer.misc;

import com.reedelk.plugin.commons.Half;
import com.reedelk.plugin.commons.Images;
import com.reedelk.plugin.graph.FlowGraph;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

import static com.reedelk.plugin.commons.Messages.Flow;

public class FlowWithErrorInfoPanel extends BaseInfoPanel {

    public FlowWithErrorInfoPanel() {
        super(Flow.ERROR_TITLE.format(), Images.Flow.Error);
    }

    @Override
    protected void drawAdditionalMessage(@NotNull FlowGraph graph,
                                         @NotNull Graphics2D g2,
                                         @NotNull JComponent parent,
                                         int yCoordinateStart) {
        super.drawAdditionalMessage(graph, g2, parent, yCoordinateStart);
        String message = Flow.ERROR_MESSAGE.format(graph.getError().getMessage());
        FlowErrorText flowErrorText = new FlowErrorText(message);
        flowErrorText.setPosition(Half.of(parent.getWidth()), yCoordinateStart);
        flowErrorText.draw(g2);
    }
}
