package com.esb.plugin.editor.designer.widget;

import com.esb.plugin.commons.Half;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.layout.ComputeMaxHeight;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.ScopedGraphNode;
import com.esb.plugin.graph.utils.FindFirstNodeOutsideScope;
import com.intellij.ui.Gray;
import com.intellij.ui.JBColor;

import java.awt.*;

import static java.awt.BasicStroke.CAP_ROUND;
import static java.awt.BasicStroke.JOIN_ROUND;

public class VerticalDivider {

    private final Stroke STROKE = new BasicStroke(1.3f, CAP_ROUND, JOIN_ROUND);
    private final JBColor VERTICAL_DIVIDER_COLOR = new JBColor(Gray._200, Gray._30);
    private final ScopedGraphNode scopedGraphNode;
    private int x;
    private int y;

    public VerticalDivider(ScopedGraphNode scopedGraphNode) {
        this.scopedGraphNode = scopedGraphNode;
    }

    public void draw(FlowGraph graph, Graphics2D graphics) {
        graphics.setStroke(STROKE);
        graphics.setColor(VERTICAL_DIVIDER_COLOR);

        int padding = (ScopedGraphNode.VERTICAL_PADDING * 2) * 2;

        GraphNode firstNodeOutsideScope = FindFirstNodeOutsideScope.of(graph, scopedGraphNode).orElse(null);

        int scopeHeight = ComputeMaxHeight.of(graph, graphics, scopedGraphNode, firstNodeOutsideScope);
        scopeHeight -= padding;

        int halfScopeHeight = Half.of(scopeHeight);

        int halfWidth = Half.of(scopedGraphNode.width(graphics));

        int verticalX = x + halfWidth;
        int verticalSeparatorMinY = y - halfScopeHeight;
        int verticalSeparatorMaxY = y + halfScopeHeight;

        graphics.drawLine(verticalX, verticalSeparatorMinY, verticalX, verticalSeparatorMaxY);
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
