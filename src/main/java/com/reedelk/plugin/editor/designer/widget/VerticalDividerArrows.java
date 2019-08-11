package com.reedelk.plugin.editor.designer.widget;

import com.reedelk.plugin.commons.Half;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.graph.node.ScopedGraphNode;

import java.awt.*;
import java.util.List;

public class VerticalDividerArrows {

    private final int verticalDividerXOffset;
    private OnProcessSuccessor callback;

    public VerticalDividerArrows(int verticalDividerXOffset, OnProcessSuccessor callback) {
        this.verticalDividerXOffset = verticalDividerXOffset;
        this.callback = callback;
    }

    public VerticalDividerArrows(int verticalDividerXOffset) {
        this.verticalDividerXOffset = verticalDividerXOffset;
    }

    public void draw(ScopedGraphNode parent, FlowGraph graph, Graphics2D graphics) {
        // Draw arrows -> perpendicular to the vertical bar.
        int halfWidth = Half.of(parent.width(graphics));
        int verticalX = parent.x() + halfWidth - verticalDividerXOffset;

        List<GraphNode> successors = graph.successors(parent);
        for (GraphNode successor : successors) {

            // We only need to draw arrows if they are inside the scope
            if (!parent.getScope().contains(successor)) {
                continue;
            }

            Point targetBaryCenter = successor.getTargetArrowEnd();
            Point sourceBaryCenter = new Point(verticalX, targetBaryCenter.y);

            Arrow arrow = new Arrow(sourceBaryCenter, targetBaryCenter);
            arrow.draw(graphics);

            if (callback != null) {
                callback.onProcess(parent, successor, graphics);
            }
        }
    }

    public interface OnProcessSuccessor {
        void onProcess(ScopedGraphNode parent, GraphNode successor, Graphics2D graphics);
    }
}
