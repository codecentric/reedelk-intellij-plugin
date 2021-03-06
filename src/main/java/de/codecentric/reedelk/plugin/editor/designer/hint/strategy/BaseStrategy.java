package de.codecentric.reedelk.plugin.editor.designer.hint.strategy;

import de.codecentric.reedelk.plugin.editor.designer.AbstractGraphNode;
import de.codecentric.reedelk.plugin.editor.designer.icon.Icon;
import de.codecentric.reedelk.plugin.graph.FlowGraph;
import de.codecentric.reedelk.plugin.graph.node.GraphNode;
import de.codecentric.reedelk.plugin.graph.node.ScopeBoundaries;
import de.codecentric.reedelk.plugin.graph.node.ScopedGraphNode;
import de.codecentric.reedelk.plugin.commons.Half;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.ImageObserver;

import static de.codecentric.reedelk.plugin.commons.Colors.DESIGNER_HINT_COLOR;
import static de.codecentric.reedelk.plugin.commons.Images.Component.PlaceholderHintIcon;
import static java.awt.BasicStroke.JOIN_MITER;

abstract class BaseStrategy implements HintStrategy {

    private static final int NODE_HINT_HEIGHT = 15;
    private static final BasicStroke STROKE = new BasicStroke(3, BasicStroke.CAP_ROUND, JOIN_MITER, 10.0f, null, 0.0f);

    void drawNodeHintAfter(@NotNull Graphics2D g2, @NotNull GraphNode hintNode) {
        int x1 = hintNode.x() + Icon.Dimension.HALF_ICON_WIDTH + 5;
        int y1 = hintNode.y() - Icon.Dimension.HALF_ICON_HEIGHT - NODE_HINT_HEIGHT;
        int x2 = hintNode.x() + Icon.Dimension.HALF_ICON_WIDTH + 5;
        int y2 = hintNode.y() - Icon.Dimension.HALF_ICON_HEIGHT + NODE_HINT_HEIGHT;
        drawLine(g2, x1, y1, x2, y2);
    }

    void drawNodeHintEnd(@NotNull Graphics2D g2, @NotNull GraphNode hintNode) {
        int x1 = AbstractGraphNode.NODE_WIDTH;
        int y1 = hintNode.y() - Icon.Dimension.HALF_ICON_HEIGHT - NODE_HINT_HEIGHT;
        int x2 = AbstractGraphNode.NODE_WIDTH;
        int y2 = hintNode.y() - Icon.Dimension.HALF_ICON_HEIGHT + NODE_HINT_HEIGHT;
        drawLine(g2, x1, y1, x2, y2);
    }

    void drawNodeHintBefore(@NotNull Graphics2D g2, @NotNull GraphNode hintNode) {
        int x1 = hintNode.x() - Icon.Dimension.ICON_WIDTH + 5;
        int y1 = hintNode.y() - Icon.Dimension.HALF_ICON_HEIGHT - NODE_HINT_HEIGHT;
        int x2 = hintNode.x() - Icon.Dimension.ICON_WIDTH + 5;
        int y2 = hintNode.y() - Icon.Dimension.HALF_ICON_HEIGHT + NODE_HINT_HEIGHT;
        drawLine(g2, x1, y1, x2, y2);
    }

    void drawVerticalBarHint(@NotNull FlowGraph graph, @NotNull Graphics2D g2, @NotNull ScopedGraphNode scopedGraphNode) {
        ScopeBoundaries scopeBoundaries = scopedGraphNode.getScopeBoundaries(graph, g2);
        int halfWidth = Half.of(scopedGraphNode.width(g2));
        int padding = ScopedGraphNode.VERTICAL_PADDING;
        int x = scopedGraphNode.x() + halfWidth - scopedGraphNode.verticalDividerXOffset();
        int y1 = scopeBoundaries.getY() + padding;
        int y2 = scopeBoundaries.getY() + scopeBoundaries.getHeight() - padding;
        drawLine(g2, x, y1, x, y2);
    }

    void drawNodeAfterVerticalBarHint(@NotNull Graphics2D g2, @NotNull ScopedGraphNode scopedGraphNode, @NotNull GraphNode successor) {
        int halfWidth = Half.of(scopedGraphNode.width(g2));
        int x1 = scopedGraphNode.x() + halfWidth - scopedGraphNode.verticalDividerXOffset();
        int y1 = successor.y() - Icon.Dimension.HALF_ICON_HEIGHT - NODE_HINT_HEIGHT;
        int x2 = scopedGraphNode.x() + halfWidth - scopedGraphNode.verticalDividerXOffset();
        int y2 = successor.y() - Icon.Dimension.HALF_ICON_HEIGHT + NODE_HINT_HEIGHT;
        drawLine(g2, x1, y1, x2, y2);
    }

    void drawEndOfScopeHint(@NotNull Graphics2D g2, @NotNull ScopeBoundaries scopeBoundaries) {
        int x = scopeBoundaries.getX() + scopeBoundaries.getWidth();
        int y1 = scopeBoundaries.getY();
        int y2 = scopeBoundaries.getY() + scopeBoundaries.getHeight();
        drawLine(g2, x, y1, x, y2);
    }

    void drawPlaceholderHint(@NotNull Graphics2D g2, @NotNull GraphNode node, @NotNull ImageObserver imageObserver) {
        int x = node.x() - Icon.Dimension.HALF_ICON_WIDTH;
        int y = node.y() - Icon.Dimension.ICON_HEIGHT;
        g2.drawImage(PlaceholderHintIcon, x, y, imageObserver);
    }

    private void drawLine(Graphics2D g2, int x1, int y1, int x2, int y2) {
        g2.setColor(DESIGNER_HINT_COLOR);
        g2.setStroke(STROKE);
        g2.drawLine(x1, y1, x2, y2);
    }
}
