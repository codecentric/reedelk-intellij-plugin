package com.esb.plugin.editor.designer;

import com.esb.plugin.commons.Half;
import com.esb.plugin.component.domain.ComponentClass;
import com.esb.plugin.component.domain.ComponentData;
import com.esb.plugin.editor.designer.widget.Arrow;
import com.esb.plugin.editor.designer.widget.ScopeBox;
import com.esb.plugin.editor.designer.widget.SelectedScopeBox;
import com.esb.plugin.editor.designer.widget.UnselectedScopeBox;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.ScopeBoundaries;
import com.esb.plugin.graph.node.ScopedGraphNode;
import com.esb.plugin.graph.utils.FindFirstNodeOutsideScope;
import com.esb.plugin.graph.utils.IsLastScopeBeforeNode;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractScopedGraphNode implements ScopedGraphNode {

    private final ComponentData componentData;
    private final ScopeBox selectedNodeScopeBox;
    private final ScopeBox unselectedNodeScopeBox;

    // x and y represent the center position
    // of this Node on the canvas.
    private int x;
    private int y;

    private boolean selected;

    private Set<GraphNode> scope = new HashSet<>();

    public AbstractScopedGraphNode(ComponentData componentData) {
        this.componentData = componentData;
        this.selectedNodeScopeBox = new SelectedScopeBox();
        this.unselectedNodeScopeBox = new UnselectedScopeBox();
    }

    @Override
    public boolean scopeContains(GraphNode node) {
        return scope.contains(node);
    }

    @Override
    public ScopeBoundaries getScopeBoundaries(FlowGraph graph, Graphics2D graphics) {
        return isSelected() ?
                selectedNodeScopeBox.getBoundaries(graph, graphics, this) :
                unselectedNodeScopeBox.getBoundaries(graph, graphics, this);
    }

    @Override
    public void draw(FlowGraph graph, Graphics2D graphics, ImageObserver observer) {
        if (isSelected()) {
            selectedNodeScopeBox.draw(graph, graphics, this);
        } else {
            unselectedNodeScopeBox.draw(graph, graphics, this);
        }
    }

    @Override
    public void drawArrows(FlowGraph graph, Graphics2D graphics, ImageObserver observer) {
        drawEndOfScopeArrow(graph, graphics);
    }

    @Override
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public int x() {
        return x;
    }

    @Override
    public int y() {
        return y;
    }

    @Override
    public ComponentData componentData() {
        return componentData;
    }

    @Override
    public void selected() {
        selected = true;
    }

    @Override
    public void unselected() {
        selected = false;
    }

    @Override
    public boolean isSelected() {
        return selected;
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

    @Override
    public ComponentClass getComponentClass() {
        return componentData.getComponentClass();
    }

    @Override
    public void drag(int x, int y) {
        // scoped node is not draggable yet.
    }

    @Override
    public void dragging() {
        // scope node is not draggable yet.
    }

    @Override
    public void drop() {
        // scope node is not draggable yet.
    }

    @Override
    public String toString() {
        return componentData.getFullyQualifiedName();
    }

    // We also need to draw a connection between the end of scope to the next successor.
    // We draw this arrow only if the last drawables of this scope connect
    // arrows in the next scope
    protected void drawEndOfScopeArrow(FlowGraph graph, Graphics2D graphics) {
        FindFirstNodeOutsideScope.of(graph, this).ifPresent(firstNodeOutsideScope -> {
            if (IsLastScopeBeforeNode.of(graph, AbstractScopedGraphNode.this, firstNodeOutsideScope)) {

                ScopeBoundaries boundaries = isSelected() ?
                        selectedNodeScopeBox.getBoundaries(graph, graphics, this) :
                        unselectedNodeScopeBox.getBoundaries(graph, graphics, this);

                Point barycenter = firstNodeOutsideScope.getBarycenter();
                Point source = new Point(boundaries.getX() + boundaries.getWidth(), barycenter.y);
                Point target = getTarget(graphics, firstNodeOutsideScope);

                Arrow arrow = new Arrow(source, target);
                arrow.draw(graphics);
            }
        });
    }

    protected Point getTarget(Graphics2D graphics, Drawable drawable) {
        Point barycenter = drawable.getBarycenter();
        return new Point(
                barycenter.x - Half.of(drawable.width(graphics)) + 15,
                barycenter.y);
    }
}
