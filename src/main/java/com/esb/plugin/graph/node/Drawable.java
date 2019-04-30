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

    default int x() {
        throw new UnsupportedOperationException();
    }

    default int y() {
        throw new UnsupportedOperationException();
    }

    default void setPosition(int x, int y) {

    }

    default int height(Graphics2D graphics) {
        throw new UnsupportedOperationException();
    }

    default int width(Graphics2D graphics) {
        throw new UnsupportedOperationException();
    }

    default boolean contains(ImageObserver observer, int x, int y) {
        throw new UnsupportedOperationException();
    }

    default void selected() {
    }

    default void unselected() {
    }

    default boolean isSelected() {
        return false;
    }

    /*
     * It is the graphical center of the Drawable. For instance,
     * A component might have an icon + text below. The bary center
     * in this case might be the center of the icon.
     */
    default Point getBarycenter(Graphics2D graphics) {
        throw new UnsupportedOperationException();
    }

    void draw(FlowGraph graph, Graphics2D graphics, ImageObserver observer);

}
