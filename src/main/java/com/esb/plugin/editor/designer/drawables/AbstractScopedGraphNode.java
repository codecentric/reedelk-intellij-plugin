package com.esb.plugin.editor.designer.drawables;

import com.esb.plugin.component.domain.ComponentData;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.Drawable;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.ScopeBoundaries;
import com.esb.plugin.graph.node.ScopedGraphNode;
import com.esb.plugin.graph.utils.FindFirstNodeOutsideScope;
import com.esb.plugin.graph.utils.IsLastScopeBeforeNode;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.util.List;
import java.util.*;

public abstract class AbstractScopedGraphNode extends AbstractGraphNode implements ScopedGraphNode {

    protected final ScopeBox scopeBox;
    protected final Drawable verticalDivider;

    private Set<GraphNode> scope = new HashSet<>();

    public AbstractScopedGraphNode(ComponentData componentData) {
        super(componentData);
        verticalDivider = new VerticalDivider(this);
        scopeBox = new ScopeBox(this);
    }

    @Override
    public boolean scopeContains(GraphNode node) {
        return scope.contains(node);
    }

    @Override
    public ScopeBoundaries getScopeBoundaries(FlowGraph graph, Graphics2D graphics) {
        return scopeBox.getBoundaries(graph, graphics);
    }

    @Override
    public void draw(FlowGraph graph, Graphics2D graphics, ImageObserver observer) {
        super.draw(graph, graphics, observer);
        verticalDivider.setPosition(x(), y());
        verticalDivider.draw(graph, graphics, observer);
        scopeBox.draw(graph, graphics, observer);
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
    public void addToScope(GraphNode node) {
        this.scope.add(node);
    }

    @Override
    public void removeFromScope(GraphNode node) {
        this.scope.remove(node);
    }

    @Override
    public Collection<GraphNode> getScope() {
        return Collections.unmodifiableSet(scope);
    }

    // We also need to draw a connection between the end of scope to the next successor.
    // We draw this arrow only if the last drawables of this scope connect
    // arrows in the next scope
    protected void drawEndOfScopeArrow(FlowGraph graph, Graphics2D graphics) {
        FindFirstNodeOutsideScope.of(graph, this).ifPresent(firstNodeOutsideScope -> {
            if (IsLastScopeBeforeNode.of(graph, AbstractScopedGraphNode.this, firstNodeOutsideScope)) {
                ScopeBoundaries boundaries = scopeBox.getBoundaries(graph, graphics);

                Point barycenter = firstNodeOutsideScope.getBarycenter(graphics);
                Point source = new Point(boundaries.getX() + boundaries.getWidth(), barycenter.y);
                Point target = getTarget(graphics, firstNodeOutsideScope);

                Arrow arrow = new Arrow(source, target);
                arrow.draw(graphics);
            }
        });
    }

    protected Point getTarget(Graphics2D graphics, Drawable drawable) {
        Point barycenter = drawable.getBarycenter(graphics);
        return new Point(
                barycenter.x - Math.floorDiv(drawable.width(graphics), 2) + 15,
                barycenter.y);
    }

}
