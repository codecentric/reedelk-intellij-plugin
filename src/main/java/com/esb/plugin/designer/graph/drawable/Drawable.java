package com.esb.plugin.designer.graph.drawable;

import com.esb.plugin.designer.editor.component.Component;
import com.esb.plugin.designer.graph.FlowGraph;

import java.awt.*;
import java.awt.image.ImageObserver;

public interface Drawable {

    // TODO: Component and display name should be removed from this interface
    Component component();

    String displayName();

    void draw(FlowGraph graph, Graphics2D graphics, ImageObserver observer);

    void drag(int x, int y);

    void dragging();

    void release();

    int y();

    int x();

    void setPosition(int x, int y);

    boolean contains(ImageObserver observer, int x, int y);

    int height(Graphics2D graphics);

    int width(Graphics2D graphics);

    void selected();

    void unselected();

    /*
     * It is the graphical center of the Drawable. For instance,
     * A component might have an icon + text below. The bary center is however
     * the center of the Icon.
     */
    Point getBaryCenter(Graphics2D graphics);

}
