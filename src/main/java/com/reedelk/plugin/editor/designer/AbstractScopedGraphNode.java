package com.reedelk.plugin.editor.designer;

import com.reedelk.plugin.commons.Half;
import com.reedelk.plugin.component.domain.ComponentClass;
import com.reedelk.plugin.component.domain.ComponentData;
import com.reedelk.plugin.editor.designer.widget.*;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.graph.node.ScopeBoundaries;
import com.reedelk.plugin.graph.node.ScopedGraphNode;
import com.reedelk.plugin.graph.utils.FindFirstNodeOutsideScope;
import com.reedelk.plugin.graph.utils.IsLastScopeBeforeNode;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.ImageObserver;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractScopedGraphNode implements ScopedGraphNode {

    private final ComponentData componentData;
    private final ScopeBox selectedNodeScopeBox;
    private final ScopeBox unselectedNodeScopeBox;
    protected final RemoveComponentIcon removeComponentIcon;

    protected final Icon icon;

    // x and y represent the center position
    // of this Node on the canvas.
    private int x;
    private int y;

    private boolean selected;

    private Set<GraphNode> scope = new HashSet<>();

    public AbstractScopedGraphNode(ComponentData componentData) {
        this.componentData = componentData;
        this.icon = new Icon(componentData);
        this.removeComponentIcon = new RemoveComponentIcon();
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
        icon.draw(graphics, observer);
        if (isSelected()) {
            drawRemoveComponentIcon(graphics, observer);
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
        this.icon.setPosition(x, y);
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
    public void drawDrag(FlowGraph graph, Graphics2D graphics, ImageObserver observer) {

    }

    @Override
    public boolean contains(ImageObserver observer, int x, int y) {
        return icon.contains(x, y);
    }

    @Override
    public void mouseMoved(DrawableListener listener, MouseEvent event) {
        int x = event.getX();
        int y = event.getY();
        if (icon.contains(x, y)) {
            listener.setTheCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
        // The hand cursor over the remove icon is visible
        // if and only if the icon is selected.
        if (selected && removeComponentIcon.withinBounds(x, y)) {
            listener.setTheCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
    }

    @Override
    public void mousePressed(DrawableListener listener, MouseEvent event) {
        int x = event.getX();
        int y = event.getY();
        // If the mouse x,y coordinates are within the remove icon,
        // and the component is currently selected, then we remove the component.
        if (selected && removeComponentIcon.withinBounds(x, y)) {
            listener.removeComponent(this);
        }
    }

    @Override
    public int bottomHalfHeight(Graphics2D graphics) {
        return icon.bottomHalfHeight(graphics);
    }

    @Override
    public int topHalfHeight(Graphics2D graphics) {
        return icon.topHalfHeight(graphics);
    }


    @Override
    public Point getTargetArrowEnd() {
        return icon.getTargetArrowEnd();
    }

    @Override
    public Point getSourceArrowStart() {
        return icon.getSourceArrowStart();
    }

    @Override
    public String toString() {
        return componentData.getFullyQualifiedName();
    }

    // We also need to draw a connection between the end of scope to the next successor.
    // We draw this arrow only if the last drawables of this scope connect
    // arrows in the next scope
    private void drawEndOfScopeArrow(FlowGraph graph, Graphics2D graphics) {
        FindFirstNodeOutsideScope.of(graph, this).ifPresent(firstNodeOutsideScope -> {
            if (IsLastScopeBeforeNode.of(graph, AbstractScopedGraphNode.this, firstNodeOutsideScope)) {

                ScopeBoundaries boundaries = isSelected() ?
                        selectedNodeScopeBox.getBoundaries(graph, graphics, this) :
                        unselectedNodeScopeBox.getBoundaries(graph, graphics, this);

                Point target = firstNodeOutsideScope.getTargetArrowEnd();
                Point source = new Point(boundaries.getX() + boundaries.getWidth(), target.y);

                Arrow arrow = new Arrow(source, target);
                arrow.draw(graphics);
            }
        });
    }

    protected void drawRemoveComponentIcon(Graphics2D graphics, ImageObserver observer) {
        int topRightX = x() + Half.of(icon.width());
        int topRightY = y() - icon.topHalfHeight(graphics) + Icon.Dimension.TOP_PADDING;
        removeComponentIcon.setPosition(topRightX, topRightY);
        removeComponentIcon.draw(graphics, observer);
    }
}
