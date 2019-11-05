package com.reedelk.plugin.component.type.fork;

import com.reedelk.plugin.component.domain.ComponentData;
import com.reedelk.plugin.editor.designer.AbstractScopedGraphNode;
import com.reedelk.plugin.editor.designer.widget.VerticalDivider;
import com.reedelk.plugin.editor.designer.widget.VerticalDividerArrows;
import com.reedelk.plugin.graph.FlowGraph;

import java.awt.*;
import java.awt.image.ImageObserver;

public class ForkNode extends AbstractScopedGraphNode {

    private final int nodeHeight = 140;
    private final int nodeWidth = 130;
    private final int verticalDividerXOffset = 7;

    private final VerticalDivider verticalDivider;
    private final VerticalDividerArrows verticalDividerArrows;


    public ForkNode(ComponentData componentData) {
        super(componentData);
        this.verticalDivider = new VerticalDivider(this);
        this.verticalDividerArrows = new VerticalDividerArrows(verticalDividerXOffset);
    }

    @Override
    public void draw(FlowGraph graph, Graphics2D graphics, ImageObserver observer) {
        super.draw(graph, graphics, observer);
        verticalDivider.draw(graph, graphics);
    }

    @Override
    public void setPosition(int x, int y) {
        super.setPosition(x, y);
        verticalDivider.setPosition(x - verticalDividerXOffset, y);
    }

    @Override
    public void drawArrows(FlowGraph graph, Graphics2D graphics, ImageObserver observer) {
        super.drawArrows(graph, graphics, observer);
        verticalDividerArrows.draw(this, graph, graphics);
    }

    @Override
    public int height(Graphics2D graphics) {
        return nodeHeight;
    }

    @Override
    public int width(Graphics2D graphics) {
        return nodeWidth;
    }

}
