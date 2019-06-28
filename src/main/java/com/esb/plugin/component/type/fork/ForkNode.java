package com.esb.plugin.component.type.fork;

import com.esb.plugin.commons.Half;
import com.esb.plugin.component.domain.ComponentData;
import com.esb.plugin.editor.designer.AbstractScopedGraphNode;
import com.esb.plugin.editor.designer.DrawableListener;
import com.esb.plugin.editor.designer.widget.Icon;
import com.esb.plugin.editor.designer.widget.VerticalDivider;
import com.esb.plugin.editor.designer.widget.VerticalDividerArrows;
import com.esb.plugin.graph.FlowGraph;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.ImageObserver;

public class ForkNode extends AbstractScopedGraphNode {

    private static final int VERTICAL_DIVIDER_X_OFFSET = 10;

    public static final int HEIGHT = 140;
    public static final int WIDTH = 130;

    private final Icon icon;
    private final VerticalDivider verticalDivider;
    private final VerticalDividerArrows verticalDividerArrows;


    public ForkNode(ComponentData componentData) {
        super(componentData);
        this.icon = new Icon(componentData);
        this.verticalDivider = new VerticalDivider(this);
        this.verticalDividerArrows = new VerticalDividerArrows(VERTICAL_DIVIDER_X_OFFSET);
    }

    @Override
    public void draw(FlowGraph graph, Graphics2D graphics, ImageObserver observer) {
        super.draw(graph, graphics, observer);
        icon.draw(graphics, observer);
        verticalDivider.draw(graph, graphics, observer);
    }

    @Override
    public void mouseMoved(DrawableListener listener, MouseEvent event) {
        int x = event.getX();
        int y = event.getY();
        if (icon.contains(x, y)) {
            listener.setTheCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
    }

    @Override
    public void mousePressed(DrawableListener listener, MouseEvent event) {
        // Nothing to do
    }

    @Override
    public void setPosition(int x, int y) {
        super.setPosition(x, y);
        icon.setPosition(x, y);
        verticalDivider.setPosition(x - VERTICAL_DIVIDER_X_OFFSET, y);
    }

    @Override
    public void drawArrows(FlowGraph graph, Graphics2D graphics, ImageObserver observer) {
        super.drawArrows(graph, graphics, observer);
        verticalDividerArrows.draw(this, graph, graphics);
    }

    @Override
    public void drawDrag(FlowGraph graph, Graphics2D graphics, ImageObserver observer) {

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
    public boolean contains(ImageObserver observer, int x, int y) {
        return icon.contains(x, y);
    }

    @Override
    public int bottomHalfHeight(Graphics2D graphics) {
        return Half.of(HEIGHT);
    }

    @Override
    public int topHalfHeight(Graphics2D graphics) {
        return Half.of(HEIGHT);
    }

    @Override
    public int height(Graphics2D graphics) {
        return HEIGHT;
    }

    @Override
    public int width(Graphics2D graphics) {
        return WIDTH;
    }

}
