package com.esb.plugin.editor.designer;

import com.esb.plugin.component.domain.ComponentData;
import com.esb.plugin.editor.designer.widget.Arrow;
import com.esb.plugin.editor.designer.widget.Icon;
import com.esb.plugin.editor.designer.widget.SelectedItem;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.ScopedGraphNode;
import com.esb.plugin.graph.utils.FindScope;
import com.esb.plugin.graph.utils.ListLastNodesOfScope;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.util.List;
import java.util.Optional;

public abstract class AbstractGraphNode implements GraphNode {

    private static final int WIDTH = 110;
    private static final int HEIGHT = 140;

    private final ComponentData componentData;

    private final Icon icon;
    private final Icon draggedIcon;
    private final SelectedItem selectedItemBox;

    // x and y represent the center position of this Node on the canvas.
    private int x;
    private int y;

    // represent the coordinates of the dragged node
    private int draggedX;
    private int draggedY;

    private boolean dragging;
    private boolean selected;

    public AbstractGraphNode(ComponentData componentData) {
        this.componentData = componentData;

        icon = new Icon(componentData);
        draggedIcon = new Icon(componentData);
        selectedItemBox = new SelectedItem(this);
    }

    @Override
    public void draw(FlowGraph graph, Graphics2D graphics, ImageObserver observer) {
        drawArrows(graph, graphics, observer);

        icon.draw(graph, graphics, observer);

        if (selected) {
            selectedItemBox.setPosition(x(), y());
            selectedItemBox.draw(graph, graphics, observer);
        }

        if (dragging) {
            draggedIcon.setPosition(draggedX, draggedY);
            draggedIcon.draw(graph, graphics, observer);
        }

        graphics.drawOval(x - 5, y - 5, 10, 10);
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
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
        this.icon.setPosition(x, y);
    }

    @Override
    public int width(Graphics2D graphics) {
        return WIDTH;
    }

    @Override
    public int height(Graphics2D graphics) {
        return HEIGHT;
    }

    @Override
    public boolean contains(ImageObserver observer, int x, int y) {
        return icon.contains(x, y);
    }

    @Override
    public boolean isSelected() {
        return selected;
    }

    @Override
    public void selected() {
        selected = true;
        selectedItemBox.selected();
    }

    @Override
    public void unselected() {
        selected = false;
        selectedItemBox.unselected();
    }

    @Override
    public void drag(int x, int y) {
        draggedX = x;
        draggedY = y;
        selectedItemBox.drag(x, y);
    }

    @Override
    public void dragging() {
        dragging = true;
        selectedItemBox.dragging();
    }

    @Override
    public void drop() {
        dragging = false;
        selectedItemBox.drop();
    }

    @Override
    public ComponentData componentData() {
        return componentData;
    }

    @Override
    public Point getBarycenter() {
        // It it is the center of the Icon.
        return icon.getBarycenter();
    }

    @Override
    public String toString() {
        return componentData.getFullyQualifiedName();
    }

    /**
     * Draws connections between this node and the next one. If this is the last
     * node of the scope, don't draw any outgoing arrow.
     */
    private void drawArrows(FlowGraph graph, Graphics2D graphics, ImageObserver observer) {
        Optional<ScopedGraphNode> wrappingScope = FindScope.of(graph, this);
        if (wrappingScope.isPresent()) {
            List<GraphNode> nodesBelongingWrappingScope = ListLastNodesOfScope.from(graph, wrappingScope.get());
            if (nodesBelongingWrappingScope.contains(this)) {
                // last node of the scope. Don't draw any outgoing arrow.
                return;
            }
        }
        drawTheArrows(graph, graphics);
    }

    private void drawTheArrows(FlowGraph graph, Graphics2D graphics) {
        graph.successors(this).forEach(successor -> {

            Point sourceBaryCenter = getBarycenter();
            Point source = new Point(
                    sourceBaryCenter.x + Math.floorDiv(60, 2) + 7,
                    sourceBaryCenter.y);

            Point targetBaryCenter = successor.getBarycenter();
            Point target = new Point(
                    targetBaryCenter.x - Math.floorDiv(60, 2) - 7,
                    targetBaryCenter.y);

            Arrow arrow = new Arrow(source, target);
            arrow.draw(graphics);
        });
    }
}
