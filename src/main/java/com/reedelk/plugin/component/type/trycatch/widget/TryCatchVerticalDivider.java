package com.reedelk.plugin.component.type.trycatch.widget;

import com.reedelk.plugin.commons.Half;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.layout.ComputeMaxHeight;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.graph.node.ScopeBoundaries;
import com.reedelk.plugin.graph.node.ScopedGraphNode;
import com.reedelk.plugin.graph.utils.FindFirstNodeOutsideScope;

import java.awt.*;
import java.util.List;

import static com.reedelk.plugin.commons.Colors.DESIGNER_SCOPE_VERTICAL_DIVIDER;
import static com.reedelk.runtime.api.commons.Preconditions.checkState;
import static java.awt.BasicStroke.CAP_ROUND;
import static java.awt.BasicStroke.JOIN_ROUND;

public class TryCatchVerticalDivider {

    private final Stroke strokeDefault = new BasicStroke(1.3f, CAP_ROUND, JOIN_ROUND);
    private final Stroke strokeDashed = new BasicStroke(1.3f, CAP_ROUND, BasicStroke.JOIN_BEVEL, 0, new float[]{3}, 0);
    private final ScopedGraphNode scopedGraphNode;
    private int x;

    public TryCatchVerticalDivider(ScopedGraphNode scopedGraphNode) {
        this.scopedGraphNode = scopedGraphNode;
    }

    public void draw(FlowGraph graph, Graphics2D graphics) {
        List<GraphNode> successors = graph.successors(scopedGraphNode);
        checkState(successors.size() == 2, "Expected exactly two successors");

        ScopeBoundaries scopeBoundaries = scopedGraphNode.getScopeBoundaries(graph, graphics);
        int currentTop = scopeBoundaries.getY();

        GraphNode firstNodeOutsideScope = FindFirstNodeOutsideScope.of(graph, scopedGraphNode).orElse(null);
        int count = 0;
        for (GraphNode successor : successors) {
            graphics.setStroke(count == 0 ? strokeDefault : strokeDashed);
            graphics.setColor(DESIGNER_SCOPE_VERTICAL_DIVIDER);

            int halfWidth = Half.of(scopedGraphNode.width(graphics));
            int verticalX = x + halfWidth - scopedGraphNode.verticalDividerXOffset();
            int maxHeight = ComputeMaxHeight.of(graph, graphics, successor, firstNodeOutsideScope);
            int verticalSeparatorMinY = currentTop + ScopedGraphNode.VERTICAL_PADDING;
            int verticalSeparatorMaxY = currentTop + maxHeight - ScopedGraphNode.VERTICAL_PADDING;

            graphics.drawLine(verticalX, verticalSeparatorMinY, verticalX, verticalSeparatorMaxY);

            currentTop += maxHeight;
            count++;
        }
    }

    public void setXPosition(int x) {
        this.x = x;
    }
}
