package com.esb.plugin.designer.graph.drawable;

import com.esb.plugin.designer.Tile;
import com.esb.plugin.designer.editor.component.Component;
import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.ScopeUtilities;
import com.esb.plugin.designer.graph.drawable.decorators.Arrow;
import com.esb.plugin.designer.graph.drawable.decorators.ScopeBoundariesDrawable;
import com.esb.plugin.designer.graph.drawable.decorators.VerticalDivider;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.util.List;
import java.util.*;

public class ChoiceDrawable extends AbstractDrawable implements ScopedDrawable {

    private final Drawable verticalDivider;
    private final ScopeBoundariesDrawable scopeBoundariesDrawable;

    private Set<Drawable> scope = new HashSet<>();

    public ChoiceDrawable(Component component) {
        super(component);
        this.verticalDivider = new VerticalDivider(this);
        this.scopeBoundariesDrawable = new ScopeBoundariesDrawable(this);
    }

    @Override
    public boolean scopeContains(Drawable drawable) {
        return scope.contains(drawable);
    }

    @Override
    public ScopeBoundaries getScopeBoundaries(FlowGraph graph, Graphics2D graphics) {
        return scopeBoundariesDrawable.getBoundaries(graph, graphics);
    }

    @Override
    public void draw(FlowGraph graph, Graphics2D graphics, ImageObserver observer) {
        super.draw(graph, graphics, observer);
        verticalDivider.draw(graph, graphics, observer);
        scopeBoundariesDrawable.draw(graph, graphics, observer);
    }

    @Override
    protected void drawConnections(FlowGraph graph, Graphics2D graphics, ImageObserver observer) {
        // Draw arrows -> perpendicular to the vertical bar.
        int halfWidth = Math.floorDiv(width(graphics), 2);
        int verticalX = x() + halfWidth - 6;

        List<Drawable> successors = graph.successors(this);
        for (Drawable successor : successors) {
            Point targetBaryCenter = successor.getBarycenter(graphics);
            Point sourceBaryCenter = new Point(verticalX, targetBaryCenter.y);
            Point target = new Point(
                    targetBaryCenter.x - Math.floorDiv(Tile.WIDTH, 2) + 15,
                    targetBaryCenter.y);
            Arrow arrow = new Arrow(sourceBaryCenter, target);
            arrow.draw(graphics);
        }

        drawEndOfScopeArrow(graph, graphics);
    }

    @Override
    public void addToScope(Drawable drawable) {
        this.scope.add(drawable);
    }

    @Override
    public void removeFromScope(Drawable drawable) {
        this.scope.remove(drawable);
    }

    @Override
    public Collection<Drawable> getScope() {
        return Collections.unmodifiableSet(scope);
    }

    // We also need to draw a connection between the end of scope to the next successor.
    // We draw this arrow only if the last drawables of this scope connect
    // arrows in the next scope
    private void drawEndOfScopeArrow(FlowGraph graph, Graphics2D graphics) {
        ScopeUtilities.getFirstNodeOutsideScope(graph, this)
                .ifPresent(firstNodeOutsideScope -> {
                    if (isLastScopeBeforeNode(graph, ChoiceDrawable.this, firstNodeOutsideScope)) {
                        Point barycenter = firstNodeOutsideScope.getBarycenter(graphics);

                        ScopeBoundaries boundaries = scopeBoundariesDrawable.getBoundaries(graph, graphics);
                        Point source = new Point(boundaries.getX() + boundaries.getWidth(),
                                barycenter.y);
                        Point target = new Point(barycenter.x - Math.floorDiv(Tile.WIDTH, 2) + 15,
                                barycenter.y);

                        Arrow arrow = new Arrow(source, target);

                        arrow.draw(graphics);
                    }
                });
    }

    static boolean isLastScopeBeforeNode(FlowGraph graph, ScopedDrawable scope, Drawable firstNodeOutsideScope) {
        Optional<ScopedDrawable> possibleScope = ScopeUtilities.findScopeOf(graph, firstNodeOutsideScope);
        return possibleScope
                .map(s -> s.scopeContains(scope))
                .orElseGet(() -> !ScopeUtilities.findScopeOf(graph, scope).isPresent());
    }

}
