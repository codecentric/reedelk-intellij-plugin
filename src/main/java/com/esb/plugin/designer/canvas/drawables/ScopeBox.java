package com.esb.plugin.designer.canvas.drawables;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.layout.FlowGraphLayoutUtils;
import com.esb.plugin.graph.node.Drawable;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.ScopeBoundaries;
import com.esb.plugin.graph.node.ScopedNode;
import com.esb.plugin.graph.scope.CountScopesBetween;
import com.esb.plugin.graph.scope.FindFirstNodeOutsideScope;
import com.esb.plugin.graph.scope.ListLastNodeOfScope;
import com.intellij.ui.Gray;
import com.intellij.ui.JBColor;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class ScopeBox implements Drawable {

    private final Stroke STROKE = new BasicStroke(1f);
    private final JBColor BOUNDARIES_COLOR = new JBColor(Gray._235, Gray._30);

    private final ScopedNode scopedNode;

    public ScopeBox(ScopedNode scopedNode) {
        this.scopedNode = scopedNode;
    }

    @Override
    public void draw(FlowGraph graph, Graphics2D graphics, ImageObserver observer) {
        graphics.setStroke(STROKE);
        graphics.setColor(BOUNDARIES_COLOR);

        ScopeBoundaries boundaries = getBoundaries(graph, graphics);
        int x = boundaries.getX();
        int y = boundaries.getY();

        graphics.drawLine(x, y, x + boundaries.getWidth(), y);
        graphics.drawLine(x + boundaries.getWidth(), y, x + boundaries.getWidth(), y + boundaries.getHeight());
        graphics.drawLine(x + boundaries.getWidth(), y + boundaries.getHeight(), x, y + boundaries.getHeight());
        graphics.drawLine(x, y + boundaries.getHeight(), x, y);
    }

    private int getMaxScopes(FlowGraph graph) {
        int max = 0;
        Collection<GraphNode> allTerminalDrawables = ListLastNodeOfScope.from(graph, scopedNode);
        for (GraphNode drawable : allTerminalDrawables) {
            Optional<Integer> scopesBetween = CountScopesBetween.them(scopedNode, drawable);
            if (scopesBetween.isPresent()) {
                max = scopesBetween.get() > max ? scopesBetween.get() : max;
            }
        }
        return max;
    }

    public ScopeBoundaries getBoundaries(FlowGraph graph, Graphics2D graphics) {
        Collection<GraphNode> drawables = ListLastNodeOfScope.from(graph, scopedNode);

        Drawable drawableWithMaxX = scopedNode;
        Drawable drawableWithMinX = scopedNode;
        Drawable drawableWithMaxY = scopedNode;
        Drawable drawableWithMinY = scopedNode;

        if (!drawables.isEmpty()) {
            List<Drawable> allDrawables = new ArrayList<>(drawables);
            allDrawables.add(scopedNode);

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

        GraphNode firstNodeOutsideScope = FindFirstNodeOutsideScope.of(graph, scopedNode).orElse(null);

        int subTreeHeight = FlowGraphLayoutUtils.maxHeight(graph, graphics, scopedNode, firstNodeOutsideScope);

        int minY = scopedNode.y() - Math.floorDiv(subTreeHeight, 2) + ScopedNode.VERTICAL_PADDING;
        int maxY = scopedNode.y() + Math.floorDiv(subTreeHeight, 2) - ScopedNode.VERTICAL_PADDING;

        // Draw Scope Boundaries we need to compute the maximum number of scopes
        int maxScopes = getMaxScopes(graph);

        int minX = drawableWithMinX.x() - Math.floorDiv(drawableWithMinX.width(graphics), 2);
        int maxX = drawableWithMaxX.x() + Math.floorDiv(drawableWithMaxX.width(graphics), 2) + (maxScopes * 5);

        int width = maxX - minX;
        int height = maxY - minY;
        return new ScopeBoundaries(minX, minY, width, height);
    }
}