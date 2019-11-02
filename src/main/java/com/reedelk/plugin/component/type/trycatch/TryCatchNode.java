package com.reedelk.plugin.component.type.trycatch;

import com.reedelk.plugin.commons.AddPlaceholder;
import com.reedelk.plugin.commons.Half;
import com.reedelk.plugin.component.domain.ComponentData;
import com.reedelk.plugin.editor.designer.AbstractScopedGraphNode;
import com.reedelk.plugin.editor.designer.widget.Arrow;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.action.remove.strategy.PlaceholderProvider;
import com.reedelk.plugin.graph.layout.ComputeMaxHeight;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.graph.node.ScopeBoundaries;
import com.reedelk.plugin.graph.utils.FindFirstNodeOutsideScope;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.util.List;

import static com.reedelk.plugin.commons.Colors.DESIGNER_VERTICAL_DIVIDER;
import static java.awt.BasicStroke.CAP_ROUND;
import static java.awt.BasicStroke.JOIN_ROUND;

public class TryCatchNode extends AbstractScopedGraphNode {

    private final int nodeHeight = 140;
    private final int nodeWidth = 130;
    private final int verticalDividerXOffset = 7;
    private final Stroke strokeDefault = new BasicStroke(1.3f, CAP_ROUND, JOIN_ROUND);
    private final Stroke strokeDashed = new BasicStroke(1.3f, CAP_ROUND, BasicStroke.JOIN_BEVEL, 0, new float[]{2}, 0);
    private final Arrow solidArrow = new Arrow();
    private final Arrow dottedArrow = new Arrow(strokeDashed);

    public TryCatchNode(ComponentData componentData) {
        super(componentData);
    }

    @Override
    public void draw(FlowGraph graph, Graphics2D graphics, ImageObserver observer) {
        super.draw(graph, graphics, observer);

        List<GraphNode> successors = graph.successors(this);

        int count = 0;
        ScopeBoundaries scopeBoundaries = getScopeBoundaries(graph, graphics);
        int currentTop = scopeBoundaries.getY();

        GraphNode firstNodeOutsideScope = FindFirstNodeOutsideScope.of(graph, this).orElse(null);
        for (GraphNode successor : successors) {

            graphics.setStroke(count == 0 ? strokeDefault : strokeDashed);
            graphics.setColor(DESIGNER_VERTICAL_DIVIDER);

            int halfWidth = Half.of(width(graphics));

            int verticalX = x() + halfWidth - verticalDividerXOffset;
            int maxHeight = ComputeMaxHeight.of(graph, graphics, successor, firstNodeOutsideScope);
            int verticalSeparatorMinY = currentTop + 3;
            int verticalSeparatorMaxY = currentTop + maxHeight - 3;

            currentTop += maxHeight;

            graphics.drawLine(verticalX, verticalSeparatorMinY, verticalX, verticalSeparatorMaxY);

            count++;
        }
    }

    @Override
    public void setPosition(int x, int y) {
        super.setPosition(x, y);
    }

    // A try/catch node can ONLY have two successors. When one of these
    // two successors is being removed a placeholder is added, therefore
    // no additional nodes can be added at the top or at the bottom of
    // any existing successor.
    @Override
    public boolean isSuccessorAllowedTop(FlowGraph graph, GraphNode successor, int index) {
        return false;
    }

    @Override
    public boolean isSuccessorAllowedBottom(FlowGraph graph, GraphNode successor, int index) {
        return false;
    }

    @Override
    public void onSuccessorRemoved(PlaceholderProvider placeholderProvider, FlowGraph graph, GraphNode removedNode, int index) {
        // If we remove a node as a successor of this scoped node, if the number
        // of successors is lower than 2 it means that
        if (graph.successors(this).size() < 2) {
            // If index == 0 we removed try
            // If index == 1 we removed catch
            // In both cases we must add a placeholder node.
            AddPlaceholder.to(placeholderProvider, graph, this, index);
        }
    }

    // When we add the Try-Catch node for the first time it does not
    // contain any node in the scope. We must add two placeholders for
    // the try and the catch flows.
    @Override
    public void commit(FlowGraph graph, PlaceholderProvider placeholderProvider) {
        if (getScope().isEmpty()) {
            AddPlaceholder.to(placeholderProvider, graph, this, 0);
            AddPlaceholder.to(placeholderProvider, graph, this, 1);
        }
    }

    @Override
    public void drawArrows(FlowGraph graph, Graphics2D graphics, ImageObserver observer) {
        super.drawArrows(graph, graphics, observer);

        // Draw arrows -> perpendicular to the vertical bar.
        int halfWidth = Half.of(width(graphics));
        int verticalX = x() + halfWidth - verticalDividerXOffset;

        List<GraphNode> successors = graph.successors(this);

        int count = 0;
        for (GraphNode successor : successors) {
            // We only need to draw arrows if they are inside the scope
            if (!getScope().contains(successor)) {
                continue;
            }

            Point targetBaryCenter = successor.getTargetArrowEnd();
            Point sourceBaryCenter = new Point(verticalX, targetBaryCenter.y);

            if (count == 0) {
                solidArrow.draw(sourceBaryCenter, targetBaryCenter, graphics);
            } else {
                dottedArrow.draw(sourceBaryCenter, targetBaryCenter, graphics);
            }
            count++;
        }
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
