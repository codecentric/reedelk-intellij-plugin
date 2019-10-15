package com.reedelk.plugin.editor.designer.widget;

import com.reedelk.plugin.commons.DebugControls;
import com.reedelk.plugin.graph.FlowSnapshot;

import java.awt.*;

public class CenterOfNodeDrawable {

    private final FlowSnapshot snapshot;

    public CenterOfNodeDrawable(FlowSnapshot snapshot) {
        this.snapshot = snapshot;
    }

    public void draw(Graphics2D g2) {
        if (DebugControls.DEBUG_DESIGNER) {
            snapshot.getGraphOrThrowIfAbsent()
                    .breadthFirstTraversal(node ->
                            g2.drawOval(node.x() - 5, node.y() - 5, 10, 10));
        }
    }
}