package com.reedelk.plugin.editor.designer.arrow;

import com.reedelk.plugin.commons.Colors;
import com.reedelk.plugin.commons.Half;
import com.reedelk.plugin.commons.IsNotScopedGraphNode;
import com.reedelk.plugin.editor.designer.AbstractGraphNode;
import com.reedelk.plugin.editor.designer.icon.Icon;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.graph.node.ScopeBoundaries;
import com.reedelk.plugin.graph.node.ScopedGraphNode;
import com.reedelk.plugin.graph.utils.FindMaxBottomHalfHeight;
import com.reedelk.plugin.graph.utils.FindScopes;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.util.Stack;

import static com.reedelk.plugin.commons.Images.Component.OutboundPlaceholderIcon;

public class OutboundEvent {

    private final int BOTTOM_PADDING = 100;
    private final int SCOPE_END_PADDING = 30;

    private final Arrow arrow;
    private final BasicStroke stroke;

    public OutboundEvent() {
        this.arrow = new Arrow();
        this.stroke = new BasicStroke(1f);
    }

    public void draw(FlowGraph graph, Graphics2D graphics, ImageObserver observer) {
        if (graph.isEmpty() || graph.nodesCount() == 1) {
            // The graph is empty or there is only root.
            // We won't draw any return event arrow.
            return;
        }

        // If there is more than one end node it must belong to an outer scope
        // which has the rightmost bound to the rightmost edge of the graph.
        graph.endNodes()
                .stream()
                .findFirst()
                .ifPresent(node -> draw(graph, graphics, observer, node));
    }

    private void draw(FlowGraph graph, Graphics2D graphics, ImageObserver observer, @NotNull GraphNode lastNode) {
        graphics.setColor(Colors.DESIGNER_ARROW);
        graphics.setStroke(stroke);

        Stack<ScopedGraphNode> scopesOfNode = FindScopes.of(graph, lastNode);
        if (scopesOfNode.isEmpty()) {
            if (IsNotScopedGraphNode.of(lastNode)) {
                int maxGraphBottomHalfHeight = FindMaxBottomHalfHeight.of(graph, graphics, graph.root(), null);
                int startX  = lastNode.x();
                int startY = lastNode.y() + lastNode.bottomHalfHeight(graphics);
                int endY = lastNode.y() + maxGraphBottomHalfHeight + BOTTOM_PADDING - Half.of(Icon.Dimension.ICON_HEIGHT);
                graphics.drawLine(startX, startY, startX, endY);
                drawOutboundPlaceholderIcon(graph, graphics, observer, startX, endY);

            } else {
                // Empty Scope Graph Node
                drawFromScope(graph, graphics, (ScopedGraphNode) lastNode, observer);
            }
        } else {
            // We take the outermost scope this node belongs to (first element of the stack)
            ScopedGraphNode scopedGraphNode = scopesOfNode.firstElement();
            drawFromScope(graph, graphics, scopedGraphNode, observer);
        }
    }

    private void drawFromScope(FlowGraph graph, Graphics2D graphics, ScopedGraphNode scopedGraphNode, ImageObserver observer) {
        ScopeBoundaries scopeBoundaries = scopedGraphNode.getScopeBoundaries(graph, graphics);

        // From End of scope to right
        int startX  = scopeBoundaries.getX() + scopeBoundaries.getWidth();
        int startY = scopeBoundaries.getY() + Half.of(scopeBoundaries.getHeight()) - Half.of(Icon.Dimension.ICON_HEIGHT);
        int endX = startX + SCOPE_END_PADDING;
        graphics.drawLine(startX, startY, endX, startY);


        // From right end of scope to bottom
        int bottomMaxHalfHeight = FindMaxBottomHalfHeight.of(graph, graphics, graph.root(), null);
        int endY = startY + bottomMaxHalfHeight + BOTTOM_PADDING;
        graphics.drawLine(endX, startY, endX, endY);

        // From bottom to beginning of flow
        drawOutboundPlaceholderIcon(graph, graphics, observer, endX, endY);
    }

    private void drawOutboundPlaceholderIcon(FlowGraph graph, Graphics2D graphics, ImageObserver observer, int startX, int endY) {
        Point source = new Point(startX, endY);
        Point target = new Point(graph.root().getSourceArrowStart().x, endY);
        arrow.draw(source, target, graphics);
        graphics.drawImage(OutboundPlaceholderIcon,
                AbstractGraphNode.HALF_NODE_WIDTH - Icon.Dimension.HALF_ICON_WIDTH,
                endY - Icon.Dimension.HALF_ICON_HEIGHT, observer);
    }
}
