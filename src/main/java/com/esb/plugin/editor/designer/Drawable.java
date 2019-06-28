package com.esb.plugin.editor.designer;

import com.esb.plugin.component.domain.ComponentClass;
import com.esb.plugin.graph.FlowGraph;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.ImageObserver;

public interface Drawable {

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

    ComponentClass getComponentClass();

    boolean contains(ImageObserver observer, int x, int y);

    void draw(FlowGraph graph, Graphics2D graphics, ImageObserver observer);

    void mouseMoved(DrawableListener listener, MouseEvent event);

    void mousePressed(DrawableListener listener, MouseEvent event);

    /**
     * Returns the point where on this node, the target arrow should end.
     */
    Point getTargetArrowEnd();

    /**
     * Returns the point where on this node, the start of  the arrow should start.
     */
    Point getSourceArrowStart();
}
