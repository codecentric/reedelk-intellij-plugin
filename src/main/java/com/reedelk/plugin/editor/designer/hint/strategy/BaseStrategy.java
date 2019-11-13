package com.reedelk.plugin.editor.designer.hint.strategy;

import com.reedelk.plugin.commons.Colors;
import com.reedelk.plugin.commons.Half;
import com.reedelk.plugin.editor.designer.widget.Icon;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.graph.node.ScopeBoundaries;
import com.reedelk.plugin.graph.node.ScopedGraphNode;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

import static com.reedelk.plugin.commons.Colors.DROP_CIRCLE_HINT;
import static java.awt.BasicStroke.JOIN_MITER;

abstract class BaseStrategy implements Strategy {

    private static final BasicStroke STROKE = new BasicStroke(3, BasicStroke.CAP_ROUND, JOIN_MITER, 10.0f, null, 0.0f);

    void drawHint(@NotNull Graphics2D g2, @NotNull GraphNode hintNode) {
        int x1 = hintNode.x() + Icon.Dimension.HALF_ICON_WIDTH + 5;
        int y1 = hintNode.y() - Icon.Dimension.HALF_ICON_HEIGHT - 15;
        int x2 = hintNode.x() + Icon.Dimension.HALF_ICON_WIDTH + 5;
        int y2 = hintNode.y() - Icon.Dimension.HALF_ICON_HEIGHT + 15;
        g2.setColor(DROP_CIRCLE_HINT);
        g2.setStroke(STROKE);
        g2.drawLine(x1, y1, x2, y2);
    }

    void drawHintAfterVerticalBar(@NotNull Graphics2D g2, @NotNull ScopedGraphNode scopedGraphNode, @NotNull GraphNode successor) {
        int halfWidth = Half.of(scopedGraphNode.width(g2));
        int x1 = scopedGraphNode.x() + halfWidth - scopedGraphNode.verticalDividerXOffset();
        int y1 = successor.y() - Icon.Dimension.HALF_ICON_HEIGHT - 15;
        int x2 = scopedGraphNode.x() + halfWidth - scopedGraphNode.verticalDividerXOffset();
        int y2 = successor.y() - Icon.Dimension.HALF_ICON_HEIGHT + 15;
        g2.setColor(DROP_CIRCLE_HINT);
        g2.setStroke(STROKE);
        g2.drawLine(x1, y1, x2, y2);
    }

    void drawEndOfScopeHint(@NotNull Graphics2D g2, @NotNull ScopeBoundaries scopeBoundaries) {
        int x = scopeBoundaries.getX() + scopeBoundaries.getWidth();
        int y1 = scopeBoundaries.getY();
        int y2 = scopeBoundaries.getY() + scopeBoundaries.getHeight();
        g2.setColor(Colors.DROP_CIRCLE_HINT);
        g2.setStroke(STROKE);
        g2.drawLine(x, y1, x, y2);
    }

    void drawHintOnVerticalBar(@NotNull FlowGraph graph, @NotNull Graphics2D g2, @NotNull ScopedGraphNode scopedGraphNode) {
        ScopeBoundaries scopeBoundaries = scopedGraphNode.getScopeBoundaries(graph, g2);
        int halfWidth = Half.of(scopedGraphNode.width(g2));
        int padding = ScopedGraphNode.VERTICAL_PADDING;
        int x = scopedGraphNode.x() + halfWidth - scopedGraphNode.verticalDividerXOffset();
        int y1 = scopeBoundaries.getY() + padding;
        int y2 = scopeBoundaries.getY() + scopeBoundaries.getHeight() - padding;
        g2.setColor(Colors.DROP_CIRCLE_HINT);
        g2.setStroke(STROKE);
        g2.drawLine(x, y1, x, y2);
    }
}
