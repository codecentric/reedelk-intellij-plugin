package com.reedelk.plugin.component.type.trycatch.widget;

import com.reedelk.plugin.commons.Half;
import com.reedelk.plugin.editor.designer.arrow.Arrow;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.graph.node.ScopedGraphNode;

import java.awt.*;
import java.util.List;

import static java.awt.BasicStroke.CAP_ROUND;

public class TryCatchVerticalDividerArrows {

    private final Stroke strokeDashed = new BasicStroke(1.3f, CAP_ROUND, BasicStroke.JOIN_BEVEL, 0, new float[]{3}, 0);

    private final Arrow solidArrow;
    private final Arrow dottedArrow;
    private final ScopedGraphNode scopedGraphNode;
    private int x;

    public TryCatchVerticalDividerArrows(ScopedGraphNode scopedGraphNode) {
        this.scopedGraphNode = scopedGraphNode;
        this.solidArrow = new Arrow();
        this.dottedArrow = new Arrow(strokeDashed);
    }

    public void draw(FlowGraph graph, Graphics2D graphics) {

        // Draw arrows -> perpendicular to the vertical bar.
        int halfWidth = Half.of(scopedGraphNode.width(graphics));
        int verticalX = x + halfWidth;

        List<GraphNode> successors = graph.successors(scopedGraphNode);

        int count = 0;
        for (GraphNode successor : successors) {
            // We only need to draw arrows if they are inside the scope
            if (!scopedGraphNode.getScope().contains(successor)) {
                continue;
            }
            Point targetBaryCenter = successor.getTargetArrowEnd();
            Point sourceBaryCenter = new Point(verticalX, targetBaryCenter.y);
            if (count == 0) {
                solidArrow.draw(sourceBaryCenter, targetBaryCenter, graphics);
            } else {
                dottedArrow.draw(sourceBaryCenter, targetBaryCenter, graphics);
            }
            count++;
        }
    }

    public void setPosition(int x, int y) {
        this.x = x;
    }
}
