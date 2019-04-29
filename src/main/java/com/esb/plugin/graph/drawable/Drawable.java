package com.esb.plugin.graph.drawable;

import com.esb.plugin.graph.FlowGraph;

import java.awt.*;
import java.awt.image.ImageObserver;

public interface Drawable {

    void drag(int x, int y);

    void dragging();

    void drop();

    void draw(FlowGraph graph, Graphics2D graphics, ImageObserver observer);

    int y();

    int x();

    void setPosition(int x, int y);

    int height(Graphics2D graphics);

    int width(Graphics2D graphics);

    boolean contains(ImageObserver observer, int x, int y);

    void selected();

    void unselected();

    boolean isSelected();

    /*
     * It is the graphical center of the Drawable. For instance,
     * A component might have an icon + text below. The bary center
     * in this case might be the center of the icon.
     */
    Point getBarycenter(Graphics2D graphics);

}
