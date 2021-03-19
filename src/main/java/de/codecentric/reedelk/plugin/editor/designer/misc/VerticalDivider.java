package de.codecentric.reedelk.plugin.editor.designer.misc;

import de.codecentric.reedelk.plugin.graph.FlowGraph;
import de.codecentric.reedelk.plugin.graph.layout.ComputeMaxHeight;
import de.codecentric.reedelk.plugin.graph.node.GraphNode;
import de.codecentric.reedelk.plugin.graph.node.ScopedGraphNode;
import de.codecentric.reedelk.plugin.graph.utils.FindFirstNodeOutsideScope;
import de.codecentric.reedelk.plugin.commons.Colors;
import de.codecentric.reedelk.plugin.commons.Half;

import java.awt.*;

import static java.awt.BasicStroke.CAP_ROUND;
import static java.awt.BasicStroke.JOIN_ROUND;

public class VerticalDivider {

    private final Color color;
    private final Stroke stroke;
    private final ScopedGraphNode scopedGraphNode;

    private int x;
    private int y;

    public VerticalDivider(ScopedGraphNode scopedGraphNode) {
        this.scopedGraphNode = scopedGraphNode;
        this.color = Colors.DESIGNER_SCOPE_VERTICAL_DIVIDER;
        this.stroke = new BasicStroke(1.3f, CAP_ROUND, JOIN_ROUND);
    }

    public void draw(FlowGraph graph, Graphics2D graphics) {
        int padding = (ScopedGraphNode.VERTICAL_PADDING * 2) * 2;

        GraphNode firstNodeOutsideScope = FindFirstNodeOutsideScope.of(graph, scopedGraphNode).orElse(null);

        int scopeHeight = ComputeMaxHeight.of(graph, graphics, scopedGraphNode, firstNodeOutsideScope);
        scopeHeight -= padding;

        int halfScopeHeight = Half.of(scopeHeight);

        int halfWidth = Half.of(scopedGraphNode.width(graphics));

        int verticalX = x + halfWidth - scopedGraphNode.verticalDividerXOffset();
        int verticalSeparatorMinY = y - halfScopeHeight;
        int verticalSeparatorMaxY = y + halfScopeHeight;

        graphics.setColor(color);
        graphics.setStroke(stroke);
        graphics.drawLine(verticalX, verticalSeparatorMinY, verticalX, verticalSeparatorMaxY);
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
