package com.esb.plugin.component.type.router.widget;

import com.esb.plugin.commons.Half;
import com.esb.plugin.component.type.router.functions.ListConditionRoutePairs;
import com.esb.plugin.editor.designer.widget.Arrow;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.ScopedGraphNode;
import com.esb.system.component.Router;

import java.awt.*;
import java.util.List;

public class VerticalDividerArrows {

    public void draw(ScopedGraphNode parent, FlowGraph graph, Graphics2D graphics) {
        // Draw arrows -> perpendicular to the vertical bar.
        int halfWidth = Half.of(parent.width(graphics));
        int verticalX = parent.x() - 60 + halfWidth;

        // We only need to draw arrows if they are inside the scope

        List<GraphNode> successors = graph.successors(parent);
        for (GraphNode successor : successors) {

            // We only draw connections to successors within this scope drawable
            if (!parent.getScope().contains(successor)) continue;

            Point targetBaryCenter = successor.getTargetArrowEnd();
            Point sourceBaryCenter = new Point(verticalX, targetBaryCenter.y);

            Arrow arrow = new Arrow(sourceBaryCenter, targetBaryCenter);
            arrow.draw(graphics);

            if (isDefaultRoute(parent, successor)) {
                graphics.setColor(Color.GRAY);
                graphics.drawString("otherwise", verticalX + 6, sourceBaryCenter.y + 14);
            }
        }
    }

    private boolean isDefaultRoute(GraphNode source, GraphNode target) {
        return ListConditionRoutePairs.of(source.componentData())
                .stream()
                .anyMatch(routerConditionRoutePair ->
                        Router.DEFAULT_CONDITION.equals(routerConditionRoutePair.getCondition()) &&
                                routerConditionRoutePair.getNext() == target);
    }
}
