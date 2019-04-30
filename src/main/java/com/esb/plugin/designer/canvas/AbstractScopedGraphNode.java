package com.esb.plugin.designer.canvas;

import com.esb.plugin.component.Component;
import com.esb.plugin.designer.canvas.drawables.Arrow;
import com.esb.plugin.designer.canvas.drawables.ScopeBoundariesDrawable;
import com.esb.plugin.designer.canvas.drawables.VerticalDivider;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.Drawable;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.ScopeBoundaries;
import com.esb.plugin.graph.node.ScopedDrawable;
import com.esb.plugin.graph.scope.FindFirstNodeOutsideScope;
import com.esb.plugin.graph.scope.IsLastScopeBeforeNode;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.util.List;
import java.util.*;

public abstract class AbstractScopedGraphNode extends AbstractGraphNode implements ScopedDrawable {

    private final Drawable verticalDivider;
    private final ScopeBoundariesDrawable scopeBoundariesDrawable;

    private Set<GraphNode> scope = new HashSet<>();

    public AbstractScopedGraphNode(Component component) {
        super(component);
        verticalDivider = new VerticalDivider(this);
        scopeBoundariesDrawable = new ScopeBoundariesDrawable(this);
    }

    @Override
    public boolean scopeContains(GraphNode drawable) {
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

        List<GraphNode> successors = graph.successors(this);
        for (Drawable successor : successors) {

            Point targetBaryCenter = successor.getBarycenter(graphics);
            Point sourceBaryCenter = new Point(verticalX, targetBaryCenter.y);
            Point target = getTarget(graphics, successor);

            Arrow arrow = new Arrow(sourceBaryCenter, target);
            arrow.draw(graphics);
        }

        drawEndOfScopeArrow(graph, graphics);
    }

    @Override
    public void addToScope(GraphNode drawable) {
        this.scope.add(drawable);
    }

    @Override
    public void removeFromScope(GraphNode drawable) {
        this.scope.remove(drawable);
    }

    @Override
    public Collection<GraphNode> getScope() {
        return Collections.unmodifiableSet(scope);
    }

    // We also need to draw a connection between the end of scope to the next successor.
    // We draw this arrow only if the last drawables of this scope connect
    // arrows in the next scope
    private void drawEndOfScopeArrow(FlowGraph graph, Graphics2D graphics) {
        FindFirstNodeOutsideScope.of(graph, this).ifPresent(firstNodeOutsideScope -> {
            if (IsLastScopeBeforeNode.of(graph, AbstractScopedGraphNode.this, firstNodeOutsideScope)) {
                ScopeBoundaries boundaries = scopeBoundariesDrawable.getBoundaries(graph, graphics);

                Point barycenter = firstNodeOutsideScope.getBarycenter(graphics);
                Point source = new Point(boundaries.getX() + boundaries.getWidth(), barycenter.y);
                Point target = getTarget(graphics, firstNodeOutsideScope);

                Arrow arrow = new Arrow(source, target);
                arrow.draw(graphics);
            }
        });
    }

    private Point getTarget(Graphics2D graphics, Drawable drawable) {
        Point barycenter = drawable.getBarycenter(graphics);
        return new Point(
                barycenter.x - Math.floorDiv(drawable.width(graphics), 2) + 15,
                barycenter.y);
    }

}
