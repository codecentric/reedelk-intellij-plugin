package com.esb.plugin.graph.node;

import com.esb.plugin.graph.FlowGraph;

import java.awt.*;
import java.awt.image.ImageObserver;

public interface Drawable {

    default void drag(int x, int y) {
    }

    default void dragging() {
    }

    default void drop() {
    }

    default void setPosition(int x, int y) {
    }

    default void selected() {
    }

    default void unselected() {
    }

    default boolean isSelected() {
        return false;
    }

    default int x() {
        throw new UnsupportedOperationException();
    }

    default int y() {
        throw new UnsupportedOperationException();
    }

    default int height(Graphics2D graphics) {
        throw new UnsupportedOperationException();
    }

    default int width(Graphics2D graphics) {
        throw new UnsupportedOperationException();
    }

    /**
     * It is the graphical center of the Drawable. For instance,
     * A component might have an icon + text below. The bary center
     * in this case might be the center of the icon.
     *
     * @param graphics the graphics used to draw the canvas.
     * @return the barycenter point of this drawable.
     */
    default Point getBarycenter(Graphics2D graphics) {
        throw new UnsupportedOperationException();
    }

    default boolean contains(ImageObserver observer, int x, int y) {
        throw new UnsupportedOperationException();
    }

    void draw(FlowGraph graph, Graphics2D graphics, ImageObserver observer);

}
