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

    int height(Graphics2D graphics);

    ComponentClass getComponentClass();

    boolean contains(ImageObserver observer, int x, int y);

    void draw(FlowGraph graph, Graphics2D graphics, ImageObserver observer);

    void mouseMoved(DrawableListener listener, MouseEvent event);

    void mousePressed(DrawableListener listener, MouseEvent event);

    /**
     * The "user perceived" center of the Drawable component.
     * For instance, a component has an icon + text below.
     * The bary center in this case is the center of the icon.
     *
     * @return the barycenter point of this drawable.
     */
    Point getBarycenter();
}
