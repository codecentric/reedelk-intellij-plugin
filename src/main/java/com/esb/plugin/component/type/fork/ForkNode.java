package com.esb.plugin.component.type.fork;

import com.esb.plugin.component.domain.ComponentData;
import com.esb.plugin.editor.designer.AbstractScopedGraphNode;
import com.esb.plugin.editor.designer.Drawable;
import com.esb.plugin.editor.designer.widget.Arrow;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.util.List;

public class ForkNode extends AbstractScopedGraphNode {

    private static final int WIDTH = 110;
    private static final int HEIGHT = 130;

    public ForkNode(ComponentData componentData) {
        super(componentData);
    }

    protected void drawArrows(FlowGraph graph, Graphics2D graphics, ImageObserver observer) {
        // Draw arrows -> perpendicular to the vertical bar.
        int halfWidth = Math.floorDiv(width(graphics), 2);
        int verticalX = x() + halfWidth - 6;

        List<GraphNode> successors = graph.successors(this);
        for (Drawable successor : successors) {

            Point targetBaryCenter = successor.getBarycenter();
            Point sourceBaryCenter = new Point(verticalX, targetBaryCenter.y);
            Point target = getTarget(graphics, successor);

            Arrow arrow = new Arrow(sourceBaryCenter, target);
            arrow.draw(graphics);
        }

        drawEndOfScopeArrow(graph, graphics, observer);
    }

    @Override
    public int width(Graphics2D graphics) {
        return WIDTH;
    }

    @Override
    public int height(Graphics2D graphics) {
        return HEIGHT;
    }

    @Override
    public Point getBarycenter() {
        return new Point();
    }

    @Override
    public boolean contains(ImageObserver observer, int x, int y) {
        return false;
    }
}
