package com.esb.plugin.graph.node;

import com.esb.plugin.component.domain.ComponentData;
import com.esb.plugin.editor.designer.DrawableListener;
import com.esb.plugin.graph.FlowGraph;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.ImageObserver;

public class NothingSelectedNode implements GraphNode {

    @Override
    public void draw(FlowGraph graph, Graphics2D graphics, ImageObserver observer) {
        // nothing to draw
    }

    @Override
    public void mouseMoved(DrawableListener listener, MouseEvent event) {

    }

    @Override
    public void mousePressed(DrawableListener listener, MouseEvent event) {

    }

    @Override
    public Point getBarycenter() {
        return null;
    }

    @Override
    public void drag(int x, int y) {

    }

    @Override
    public void dragging() {

    }

    @Override
    public void drop() {

    }

    @Override
    public void setPosition(int x, int y) {

    }

    @Override
    public void selected() {

    }

    @Override
    public void unselected() {

    }

    @Override
    public void drawArrows(FlowGraph graph, Graphics2D graphics, ImageObserver observer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void drawDrag(FlowGraph graph, Graphics2D graphics, ImageObserver observer) {

    }

    @Override
    public int x() {
        return 0;
    }

    @Override
    public int y() {
        return 0;
    }

    @Override
    public boolean isInbound() {
        return false;
    }

    @Override
    public boolean isSelected() {
        return false;
    }

    @Override
    public int height(Graphics2D graphics) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int width(Graphics2D graphics) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean contains(ImageObserver observer, int x, int y) {
        return false;
    }

    @Override
    public ComponentData componentData() {
        return null;
    }

}
