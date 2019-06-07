package com.esb.plugin.editor.designer.widget;

import com.esb.plugin.commons.Half;
import com.esb.plugin.editor.designer.Drawable;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.layout.utils.ComputeMaxHeight;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.ScopeBoundaries;
import com.esb.plugin.graph.node.ScopedGraphNode;
import com.esb.plugin.graph.utils.CountScopesBetween;
import com.esb.plugin.graph.utils.FindFirstNodeOutsideScope;
import com.esb.plugin.graph.utils.ListLastNodesOfScope;
import com.intellij.ui.JBColor;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public abstract class ScopeBox {

    private final int IN_BETWEEN_SCOPES_PADDING = 5;

    private Stroke stroke;
    private JBColor boundariesColor;
    private ScopedGraphNode scopedGraphNode;

    ScopeBox(ScopedGraphNode scopedGraphNode, JBColor boundariesColor, Stroke stroke) {
        this.stroke = stroke;
        this.boundariesColor = boundariesColor;
        this.scopedGraphNode = scopedGraphNode;
    }

    public void draw(FlowGraph graph, Graphics2D graphics, ImageObserver observer) {
        graphics.setStroke(stroke);
        graphics.setColor(boundariesColor);

        ScopeBoundaries boundaries = getBoundaries(graph, graphics);
        int x = boundaries.getX();
        int y = boundaries.getY();

        // Scope Box
        //  ----------------
        //  |              |
        //  |              |
        //                 |
        //                 |
        //  |              |
        //  |              |
        //  ----------------
        int topLeftX = x;
        int topLeftY = y;
        int topRightX = x + boundaries.getWidth();
        int topRightY = y;
        int bottomRightX = x + boundaries.getWidth();
        int bottomRightY = y + boundaries.getHeight();
        int bottomLeftX = x;
        int bottomLeftY = y + boundaries.getHeight();

        int midBottomLeftX = x;
        int midBottomLeftY = scopedGraphNode.getBarycenter().y + 20;
        int midTopLeftX = x;
        int midTopLeftY = scopedGraphNode.getBarycenter().y - 20;

        graphics.drawLine(topLeftX, topLeftY, topRightX, topRightY);
        graphics.drawLine(topRightX, topLeftY, bottomRightX, bottomRightY);
        graphics.drawLine(bottomRightX, bottomRightY, bottomLeftX, bottomLeftY);

        graphics.drawLine(bottomLeftX, bottomLeftY, midBottomLeftX, midBottomLeftY);
        graphics.drawLine(midTopLeftX, midTopLeftY, topLeftX, topLeftY);
    }

    private int getMaxScopes(FlowGraph graph) {
        int max = 0;
        Collection<GraphNode> allTerminalNodes = ListLastNodesOfScope.from(graph, scopedGraphNode);
        for (GraphNode node : allTerminalNodes) {
            Optional<Integer> scopesBetween = CountScopesBetween.them(scopedGraphNode, node);
            if (scopesBetween.isPresent()) {
                max = scopesBetween.get() > max ? scopesBetween.get() : max;
            }
        }
        return max;
    }

    public ScopeBoundaries getBoundaries(FlowGraph graph, Graphics2D graphics) {
        Collection<GraphNode> nodes = ListLastNodesOfScope.from(graph, scopedGraphNode);

        Drawable drawableWithMaxX = scopedGraphNode;
        Drawable drawableWithMinX = scopedGraphNode;
        Drawable drawableWithMaxY = scopedGraphNode;
        Drawable drawableWithMinY = scopedGraphNode;

        if (!nodes.isEmpty()) {
            Set<Drawable> allDrawables = new HashSet<>(nodes);
            allDrawables.add(scopedGraphNode);

            // We need to find min x, max x, min y and max y
            for (Drawable drawable : allDrawables) {
                if (drawableWithMaxX.x() < drawable.x()) {
                    drawableWithMaxX = drawable;
                }
                if (drawableWithMinX.x() > drawable.x()) {
                    drawableWithMinX = drawable;
                }
                if (drawableWithMaxY.y() < drawable.y()) {
                    drawableWithMaxY = drawable;
                }
                if (drawableWithMinY.y() > drawable.y()) {
                    drawableWithMinY = drawable;
                }
            }
        }

        GraphNode firstNodeOutsideScope = FindFirstNodeOutsideScope.of(graph, scopedGraphNode).orElse(null);

        int subTreeHeight = ComputeMaxHeight.of(graph, graphics, scopedGraphNode, firstNodeOutsideScope);

        int halfSubTreeHeight = Half.of(subTreeHeight);

        int minY = scopedGraphNode.y() - halfSubTreeHeight + ScopedGraphNode.VERTICAL_PADDING;
        int maxY = scopedGraphNode.y() + halfSubTreeHeight - ScopedGraphNode.VERTICAL_PADDING;

        // Draw Scope Boundaries we need to compute the maximum number of scopes
        int maxScopes = getMaxScopes(graph);

        int minX = drawableWithMinX.x() - Half.of(drawableWithMinX.width(graphics));
        int maxX = drawableWithMaxX.x() + Half.of(drawableWithMaxX.width(graphics)) + (maxScopes * IN_BETWEEN_SCOPES_PADDING);

        int width = maxX - minX;
        int height = maxY - minY;
        return new ScopeBoundaries(minX, minY, width, height);
    }
}
