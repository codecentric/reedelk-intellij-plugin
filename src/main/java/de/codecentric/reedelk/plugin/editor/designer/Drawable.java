package de.codecentric.reedelk.plugin.editor.designer;

import de.codecentric.reedelk.plugin.graph.FlowGraph;
import de.codecentric.reedelk.module.descriptor.model.component.ComponentType;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.ImageObserver;

public interface Drawable {

    boolean isDraggable();

    boolean isSelectable();

    void drag(int x, int y);

    void dragging();

    void drop();

    void selected();

    void unselected();

    boolean isSelected();

    void drawArrows(FlowGraph graph, Graphics2D graphics, ImageObserver observer);

    void drawDrag(FlowGraph graph, Graphics2D graphics, ImageObserver observer);

    int x();

    int y();

    void setPosition(int x, int y);

    int width(Graphics2D graphics);

    int topHalfHeight(Graphics2D graphics);

    int bottomHalfHeight(Graphics2D graphics);

    int height(Graphics2D graphics);

    boolean contains(ImageObserver observer, int x, int y);

    void draw(FlowGraph graph, Graphics2D graphics, ImageObserver observer);

    void mouseMoved(DrawableListener listener, MouseEvent event);

    void mousePressed(DrawableListener listener, MouseEvent event);

    /**
     * Returns the type of the component: inbound, join, processor and so on.
     */
    ComponentType getComponentType();

    /**
     * Returns the point where on this node, the target arrow should end.
     */
    Point getTargetArrowEnd();

    /**
     * Returns the point where on this node, the start of  the arrow should start.
     */
    Point getSourceArrowStart();
}
