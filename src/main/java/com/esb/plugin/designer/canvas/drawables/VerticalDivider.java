package com.esb.plugin.designer.canvas.drawables;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.Drawable;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.ScopedDrawable;
import com.esb.plugin.graph.scope.FindFirstNodeOutsideScope;
import com.intellij.ui.Gray;
import com.intellij.ui.JBColor;

import java.awt.*;
import java.awt.image.ImageObserver;

import static com.esb.plugin.graph.layout.FlowGraphLayoutUtils.maxHeight;
import static java.awt.BasicStroke.CAP_ROUND;
import static java.awt.BasicStroke.JOIN_ROUND;

public class VerticalDivider implements Drawable {

    private final Stroke STROKE = new BasicStroke(1.3f, CAP_ROUND, JOIN_ROUND);
    private final JBColor VERTICAL_DIVIDER_COLOR = new JBColor(Gray._200, Gray._30);

    private final ScopedDrawable scopedDrawable;

    public VerticalDivider(ScopedDrawable scopedDrawable) {
        this.scopedDrawable = scopedDrawable;
    }

    @Override
    public void draw(FlowGraph graph, Graphics2D graphics, ImageObserver observer) {
        graphics.setStroke(STROKE);
        graphics.setColor(VERTICAL_DIVIDER_COLOR);

        int padding = (ScopedDrawable.VERTICAL_PADDING * 4) * 2;

        GraphNode firstNodeOutsideScope = FindFirstNodeOutsideScope.of(graph, scopedDrawable).orElse(null);

        int scopeHeight = maxHeight(graph, graphics, scopedDrawable, firstNodeOutsideScope);
        scopeHeight -= padding;

        int halfScopeHeight = Math.floorDiv(scopeHeight, 2);

        int halfWidth = Math.floorDiv(scopedDrawable.width(graphics), 2);

        int verticalX = scopedDrawable.x() + halfWidth - 6;
        int verticalSeparatorMinY = scopedDrawable.y() - halfScopeHeight;
        int verticalSeparatorMaxY = scopedDrawable.y() + halfScopeHeight;

        graphics.drawLine(verticalX, verticalSeparatorMinY, verticalX, verticalSeparatorMaxY);
    }

}
