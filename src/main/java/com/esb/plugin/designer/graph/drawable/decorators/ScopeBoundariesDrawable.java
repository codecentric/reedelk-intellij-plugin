package com.esb.plugin.designer.graph.drawable.decorators;

import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.ScopeUtilities;
import com.esb.plugin.designer.graph.drawable.Drawable;
import com.esb.plugin.designer.graph.drawable.ScopedDrawable;
import com.esb.plugin.designer.graph.layout.FlowGraphLayout;
import com.intellij.ui.Gray;
import com.intellij.ui.JBColor;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class ScopeBoundariesDrawable implements Drawable {

    private final Stroke STROKE = new BasicStroke(1f);
    private final JBColor BOUNDARIES_COLOR = new JBColor(Gray._235, Gray._30);

    private final ScopedDrawable scopedDrawable;

    public ScopeBoundariesDrawable(ScopedDrawable scopedDrawable) {
        this.scopedDrawable = scopedDrawable;
    }

    @Override
    public void draw(FlowGraph graph, Graphics2D graphics, ImageObserver observer) {
        graphics.setStroke(STROKE);
        graphics.setColor(BOUNDARIES_COLOR);

        Collection<Drawable> drawables = ScopeUtilities.listLastDrawablesOfScope(graph, scopedDrawable);

        Drawable drawableWithMaxX = scopedDrawable;
        Drawable drawableWithMinX = scopedDrawable;
        Drawable drawableWithMaxY = scopedDrawable;
        Drawable drawableWithMinY = scopedDrawable;

        if (!drawables.isEmpty()) {
            List<Drawable> allDrawables = new ArrayList<>(drawables);
            allDrawables.add(scopedDrawable);

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

        int subTreeHeight = FlowGraphLayout.computeSubTreeHeight(graph, scopedDrawable, graphics);
        int minY = scopedDrawable.y() - Math.floorDiv(subTreeHeight, 2) + ScopedDrawable.VERTICAL_PADDING;
        int maxY = scopedDrawable.y() + Math.floorDiv(subTreeHeight, 2) - ScopedDrawable.VERTICAL_PADDING;

        // Draw Scope Boundaries we need to compute the maximum number of scopes
        int maxScopes = getMaxScopes(graph);

        int point1X = drawableWithMinX.x() - Math.floorDiv(drawableWithMinX.width(graphics), 2);
        int point2X = drawableWithMaxX.x() + Math.floorDiv(drawableWithMaxX.width(graphics), 2) + (maxScopes * 5);
        int point3X = drawableWithMaxX.x() + Math.floorDiv(drawableWithMaxX.width(graphics), 2) + (maxScopes * 5);
        int point4X = drawableWithMinX.x() - Math.floorDiv(drawableWithMinX.width(graphics), 2);

        graphics.drawLine(point1X, minY, point2X, minY);
        graphics.drawLine(point2X, minY, point3X, maxY);
        graphics.drawLine(point3X, maxY, point4X, maxY);
        graphics.drawLine(point4X, maxY, point1X, minY);
    }

    @Override
    public void drag(int x, int y) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void dragging() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void drop() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int y() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int x() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setPosition(int x, int y) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean contains(ImageObserver observer, int x, int y) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int height(Graphics2D graphics) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int width(Graphics2D graphics) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isSelected() {
        return false;
    }

    @Override
    public void selected() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void unselected() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Point getBarycenter(Graphics2D graphics) {
        throw new UnsupportedOperationException();
    }

    private int getMaxScopes(FlowGraph graph) {
        int max = 0;
        Collection<Drawable> allTerminalDrawables = ScopeUtilities.listLastDrawablesOfScope(graph, scopedDrawable);
        for (Drawable drawable : allTerminalDrawables) {
            Optional<Integer> scopesBetween = ScopeUtilities.scopesBetween(scopedDrawable, drawable);
            if (scopesBetween.isPresent()) {
                max = scopesBetween.get() > max ? scopesBetween.get() : max;
            }
        }
        return max;
    }
}
