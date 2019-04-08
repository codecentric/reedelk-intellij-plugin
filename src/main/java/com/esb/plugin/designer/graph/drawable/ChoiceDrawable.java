package com.esb.plugin.designer.graph.drawable;

import com.esb.plugin.designer.Tile;
import com.esb.plugin.designer.editor.component.Component;
import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.dnd.ScopeUtilities;
import com.esb.plugin.designer.graph.layout.FlowGraphLayout;
import com.intellij.ui.Gray;
import com.intellij.ui.JBColor;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.util.List;
import java.util.*;

public class ChoiceDrawable extends AbstractDrawable implements ScopedDrawable {

    private Set<Drawable> scope = new HashSet<>();

    public ChoiceDrawable(Component component) {
        super(component);
    }

    public void addToScope(Drawable drawable) {
        this.scope.add(drawable);
    }

    public void removeFromScope(Drawable drawable) {
        this.scope.remove(drawable);
    }

    public Collection<Drawable> getScope() {
        return Collections.unmodifiableSet(scope);
    }

    @Override
    public boolean scopeContains(Drawable drawable) {
        return scope.contains(drawable);
    }

    @Override
    public void draw(FlowGraph graph, Graphics graphics, ImageObserver observer) {
        super.draw(graph, graphics, observer);
        paintVerticalBar(graphics);
        paintScopeBoundaries(graph, graphics);
    }

    private void paintVerticalBar(Graphics graphics) {
        java.util.List<Drawable> drawablesInTheScope = new ArrayList<Drawable>();
        drawablesInTheScope.add(this);
        drawablesInTheScope.addAll(getScope());

        int minY = drawablesInTheScope.stream().mapToInt(Drawable::y).min().getAsInt();
        int maxY = drawablesInTheScope.stream().mapToInt(Drawable::y).max().getAsInt();

        int verticalX = x() + Tile.HALF_WIDTH - 6;
        int verticalMinY = minY - Math.floorDiv(Tile.HEIGHT, 3);
        int verticalMaxY = maxY + Math.floorDiv(Tile.HEIGHT, 3);

        graphics.setColor(new JBColor(Gray._200, Gray._30));
        graphics.drawLine(verticalX, verticalMinY, verticalX, verticalMaxY);
    }

    private void paintScopeBoundaries(FlowGraph graph, Graphics graphics) {

        Collection<Drawable> drawables = ScopeUtilities.listLastDrawablesOfScope(graph, this);

        Drawable drawableWithMaxX = this;
        Drawable drawableWithMinX = this;
        Drawable drawableWithMaxY = this;
        Drawable drawableWithMinY = this;


        if (!drawables.isEmpty()) {
            List<Drawable> allDrawables = new ArrayList<>(drawables);
            allDrawables.add(this);
            // We need to find max x
            for (Drawable drawable : allDrawables) {
                if (drawableWithMaxX.x() < drawable.x()) {
                    drawableWithMaxX = drawable;
                }
            }

            for (Drawable drawable : allDrawables) {
                if (drawableWithMinX.x() > drawable.x()) {
                    drawableWithMinX = drawable;
                }
            }

            for (Drawable drawable : allDrawables) {
                if (drawableWithMaxY.y() < drawable.y()) {
                    drawableWithMaxY = drawable;
                }
            }
            for (Drawable drawable : allDrawables) {
                if (drawableWithMinY.y() > drawable.y()) {
                    drawableWithMinY = drawable;
                }
            }
        }


        int subTreeHeight = FlowGraphLayout.computeSubTreeHeight(graph, this);
        int minY = y() - Math.floorDiv(subTreeHeight, 2) + ScopedDrawable.VERTICAL_PADDING;
        int maxY = y() + Math.floorDiv(subTreeHeight, 2) - ScopedDrawable.VERTICAL_PADDING;

        // Draw Scope Boundaries
        int maxScopes = getMaxScopes(graph, this);


        int line1X = drawableWithMinX.x() - Math.floorDiv(drawableWithMinX.width(), 2);
        int line2X = drawableWithMaxX.x() + Math.floorDiv(drawableWithMaxX.width(), 2) + (maxScopes * 5);
        int line3X = drawableWithMaxX.x() + Math.floorDiv(drawableWithMaxX.width(), 2) + (maxScopes * 5);
        int line4X = drawableWithMinX.x() - Math.floorDiv(drawableWithMinX.width(), 2);
        graphics.setColor(new JBColor(Gray._235, Gray._30));
        graphics.drawLine(line1X, minY, line2X, minY);
        graphics.drawLine(line2X, minY, line3X, maxY);
        graphics.drawLine(line3X, maxY, line4X, maxY);
        graphics.drawLine(line4X, maxY, line1X, minY);
    }

    private int getMaxScopes(FlowGraph graph, ScopedDrawable scopedDrawable) {
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
