package com.esb.plugin.component.type.fork;

import com.esb.plugin.commons.Half;
import com.esb.plugin.component.domain.ComponentData;
import com.esb.plugin.editor.designer.AbstractScopedGraphNode;
import com.esb.plugin.editor.designer.widget.VerticalDivider;
import com.esb.plugin.editor.designer.widget.VerticalDividerArrows;
import com.esb.plugin.graph.FlowGraph;

import java.awt.*;
import java.awt.image.ImageObserver;

public class ForkNode extends AbstractScopedGraphNode {

    private static final int VERTICAL_DIVIDER_X_OFFSET = 7;

    public static final int HEIGHT = 140;
    public static final int WIDTH = 130;

    private final VerticalDivider verticalDivider;
    private final VerticalDividerArrows verticalDividerArrows;


    public ForkNode(ComponentData componentData) {
        super(componentData);
        this.verticalDivider = new VerticalDivider(this);
        this.verticalDividerArrows = new VerticalDividerArrows(VERTICAL_DIVIDER_X_OFFSET);
    }

    @Override
    public void draw(FlowGraph graph, Graphics2D graphics, ImageObserver observer) {
        super.draw(graph, graphics, observer);
        verticalDivider.draw(graph, graphics, observer);
    }

    @Override
    public void setPosition(int x, int y) {
        super.setPosition(x, y);
        verticalDivider.setPosition(x - VERTICAL_DIVIDER_X_OFFSET, y);
    }

    @Override
    public void drawArrows(FlowGraph graph, Graphics2D graphics, ImageObserver observer) {
        super.drawArrows(graph, graphics, observer);
        verticalDividerArrows.draw(this, graph, graphics);
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
